package com.sirrlevi.whatsupp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Plain data for one message bubble, so the bubble UI can be drawn (and rendered in tests) without a database. */
internal data class WaQuote(
    val title: String,
    val fromMe: Boolean,
    val text: String,
    val kind: String = "TEXT",
    val thumbnailUri: String? = null
)

internal data class WaBubble(
    val id: String,
    val outgoing: Boolean,
    val text: String,
    val kind: String,
    val attachmentUri: String?,
    val time: String,
    val status: String,
    val edited: Boolean,
    val reactions: List<String>,
    val quote: WaQuote?,
    /** Only the first bubble of a run from the same sender carries the little tail. */
    val tail: Boolean,
    /** Vertical gap above this row in reference pixels (4 inside a run, 27.5 between runs). */
    val gapAbove: Float
)

internal const val BUBBLE_RADIUS = 27.5f
internal const val BUBBLE_MAX_WIDTH = 876f
internal const val BUBBLE_SIDE_MARGIN = 46.5f

/** Rounded bubble with the small tail used by the first message of a run. The tail is drawn outside the bounds. */
internal class WaBubbleShape(private val outgoing: Boolean, private val tail: Boolean) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val s = density.density / REFERENCE_DENSITY
        val w = size.width
        val h = size.height
        val r = min(BUBBLE_RADIUS * s, min(w, h) / 2f)
        val path = Path()
        val tipOut = 18.5f * s
        val tipCurve = 15.5f * s
        val tipTop = 3.5f * s
        val tipBottom = 6.5f * s
        val joinY = 25.5f * s
        if (tail && !outgoing) {
            path.moveTo(-tipCurve, 0f)
        } else {
            path.moveTo(r, 0f)
        }
        if (tail && outgoing) {
            path.lineTo(w + tipCurve, 0f)
            path.quadraticBezierTo(w + tipOut, 0f, w + tipOut, tipTop)
            path.lineTo(w + tipOut, tipBottom)
            path.lineTo(w, joinY)
        } else {
            path.lineTo(w - r, 0f)
            path.arcTo(Rect(w - 2 * r, 0f, w, 2 * r), -90f, 90f, false)
        }
        path.lineTo(w, h - r)
        path.arcTo(Rect(w - 2 * r, h - 2 * r, w, h), 0f, 90f, false)
        path.lineTo(r, h)
        path.arcTo(Rect(0f, h - 2 * r, 2 * r, h), 90f, 90f, false)
        if (tail && !outgoing) {
            path.lineTo(0f, joinY)
            path.lineTo(-tipOut, tipBottom)
            path.lineTo(-tipOut, tipTop)
            path.quadraticBezierTo(-tipOut, 0f, -tipCurve, 0f)
        } else {
            path.lineTo(0f, r)
            path.arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
        }
        path.close()
        return Outline.Generic(path)
    }
}

private const val TICK_W = 45.5f
private const val TICK_H = 29.5f

/** The delivery ticks: one check for sent, two for delivered, two blue ones for seen. */
@Composable
internal fun WaTicks(status: String, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(TICK_W.px, TICK_H.px)) {
        val s = size.width / TICK_W
        val stroke = Stroke(width = 4.5f * s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun check(dx: Float): Path = Path().apply {
            moveTo((3f + dx) * s, 17.5f * s)
            lineTo((10.5f + dx) * s, 27f * s)
            lineTo((28.5f + dx) * s, 2.5f * s)
        }
        if (status == "SENT") {
            drawPath(check(14f), color, style = stroke)
        } else {
            drawPath(check(0f), color, style = stroke)
            drawPath(check(14f), color, style = stroke)
        }
    }
}

/** Time (and ticks for outgoing messages) shown in the bottom-right corner of a bubble. */
@Composable
internal fun WaMeta(
    time: String,
    edited: Boolean,
    outgoing: Boolean,
    status: String,
    modifier: Modifier = Modifier,
    colorOverride: Color? = null
) {
    val p = LocalWaPalette.current
    val color = colorOverride ?: if (outgoing) p.metaOutgoing else p.metaIncoming
    val tickColor = if (status == "READ") p.tickRead else color
    val label = if (edited) "Edited $time" else time
    Layout(
        modifier = modifier,
        content = {
            BasicText(label, style = WaText.time.copy(color = color), maxLines = 1, softWrap = false)
            if (outgoing) WaTicks(status, tickColor)
        }
    ) { measurables, _ ->
        val s = density / REFERENCE_DENSITY
        val timeP = measurables[0].measure(Constraints())
        val tickP = measurables.getOrNull(1)?.measure(Constraints())
        val baseline = timeP[LastBaseline]
        val tickAbove = (26.5f * s).roundToInt()
        val tickBelow = (3f * s).roundToInt()
        val gap = (15.7f * s).roundToInt()
        val above = max(baseline, if (tickP != null) tickAbove else 0)
        val below = max(timeP.height - baseline, if (tickP != null) tickBelow else 0)
        val width = timeP.width + if (tickP != null) gap + tickP.width else 0
        layout(width, above + below, mapOf(LastBaseline to above)) {
            timeP.placeRelative(0, above - baseline)
            tickP?.placeRelative(timeP.width + gap, above - tickAbove)
        }
    }
}

/** The grey block that shows the message being replied to, with a coloured bar on its left side. */
@Composable
internal fun WaQuote(quote: WaQuote, outgoingBubble: Boolean, modifier: Modifier = Modifier) {
    val p = LocalWaPalette.current
    val bg = if (outgoingBubble) p.quoteOutgoing else p.quoteIncoming
    val bar = if (quote.fromMe) p.green else p.purple
    val titleColor = if (quote.fromMe) p.titleMe else p.titleThem
    val bodyColor = if (outgoingBubble) p.quoteTextOutgoing else p.secondary
    val hasThumb = quote.kind == "IMAGE" && !quote.thumbnailUri.isNullOrBlank()
    Box(
        modifier
            .clip(RoundedCornerShape(20.px))
            .background(bg)
            .drawBehind { drawRect(bar, size = Size(8.25f * density / REFERENCE_DENSITY, size.height)) }
    ) {
        Column(
            Modifier.padding(
                start = 32.px,
                top = 22.px,
                end = if (hasThumb) (132 + 22).px else 22.px,
                bottom = 20.px
            )
        ) {
            BasicText(
                quote.title,
                style = WaText.quoteTitle.copy(color = titleColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val icon = when (quote.kind) {
                "IMAGE" -> Icons.Outlined.Image
                "VIDEO" -> Icons.Outlined.Videocam
                "AUDIO" -> Icons.Outlined.Mic
                "FILE" -> Icons.Outlined.Description
                else -> null
            }
            if (icon != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = bodyColor, modifier = Modifier.size(38.px))
                    Spacer(Modifier.width(8.px))
                    BasicText(
                        quote.text,
                        style = WaText.quoteBody.copy(color = bodyColor),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                BasicText(
                    quote.text,
                    style = WaText.quoteBody.copy(color = bodyColor),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (hasThumb) {
            AsyncImage(
                model = quote.thumbnailUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.align(Alignment.CenterEnd).size(132.px).clip(RoundedCornerShape(topEnd = 20.px, bottomEnd = 20.px))
            )
        }
    }
}

/**
 * Text bubble content: optional quote on top, message text, and the time/ticks tucked into the bottom-right corner.
 * When the last line of text has no room left for the time, the time moves to its own row, as in the reference.
 */
@Composable
internal fun WaTextBubbleBody(
    text: String,
    outgoing: Boolean,
    header: (@Composable (Modifier) -> Unit)?,
    meta: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    overlayMeta: Boolean = false
) {
    val p = LocalWaPalette.current
    val measurer = rememberTextMeasurer()
    val style = WaText.message.copy(color = p.text)
    Layout(
        modifier = modifier,
        content = {
            if (header != null) header(Modifier.layoutId("quote"))
            if (text.isNotEmpty()) BasicText(text, Modifier.layoutId("text"), style = style)
            meta(Modifier.layoutId("meta"))
        }
    ) { measurables, constraints ->
        val s = density / REFERENCE_DENSITY
        fun r(v: Float) = (v * s).roundToInt()
        val quoteM = measurables.firstOrNull { it.layoutId == "quote" }
        val textM = measurables.firstOrNull { it.layoutId == "text" }
        val metaM = measurables.first { it.layoutId == "meta" }

        val padL = r(27.5f)
        val padR = r(27.5f)
        val inset = r(11f)
        val maxBubble = min(r(BUBBLE_MAX_WIDTH), constraints.maxWidth)
        val metaP = metaM.measure(Constraints())
        val metaInsetR = if (outgoing) r(25.5f) else r(23.2f)
        val metaGap = if (outgoing) r(23.5f) else r(40f)
        val textMax = maxBubble - padL - padR

        if (overlayMeta && textM == null && quoteM != null) {
            val natural = min(quoteM.maxIntrinsicWidth(Constraints.Infinity), maxBubble - 2 * inset) + 2 * inset
            val ow = min(max(natural, metaP.width + 2 * inset), maxBubble)
            val headerP = quoteM.measure(Constraints.fixedWidth(ow - 2 * inset))
            val oh = headerP.height + 2 * inset
            val baseline = metaP[LastBaseline]
            layout(ow, oh) {
                headerP.placeRelative(inset, inset)
                metaP.placeRelative(
                    ow - inset - r(16f) - metaP.width,
                    oh - inset - r(12f) - baseline - r(6f)
                )
            }
        } else {
        var textW = 0
        var textH = 0
        var lastRight = 0
        var textP: Placeable? = null
        if (textM != null) {
            val res = measurer.measure(
                text = text,
                style = style,
                softWrap = true,
                constraints = Constraints(maxWidth = textMax),
                layoutDirection = layoutDirection,
                density = this
            )
            textW = res.size.width
            textH = res.size.height
            lastRight = ceil(res.getLineRight(res.lineCount - 1)).toInt()
            textP = textM.measure(Constraints.fixed(textW, textH))
        }

        val needForMeta = padL + lastRight + metaGap + metaP.width + metaInsetR
        val metaOnLastLine = textM == null || needForMeta <= maxBubble
        var w = if (textM == null) {
            padL + metaP.width + metaInsetR
        } else if (metaOnLastLine) {
            max(padL + textW + padR, needForMeta)
        } else {
            max(padL + textW + padR, padL + metaP.width + metaInsetR)
        }
        if (quoteM != null) {
            val natural = min(quoteM.maxIntrinsicWidth(Constraints.Infinity), maxBubble - 2 * inset) + 2 * inset
            w = max(w, natural)
        }
        w = min(w, maxBubble)
        val quoteP = quoteM?.measure(Constraints.fixedWidth(w - 2 * inset))

        val textY = if (quoteP != null) inset + quoteP.height + r(11.7f) else r(15.7f)
        var h = textY + textH + r(25.3f)
        if (!metaOnLastLine) h += r(38.5f)
        val metaBaseline = metaP[LastBaseline]
        val metaY = h - (if (metaOnLastLine) r(19.5f) else r(17.5f)) - metaBaseline
        layout(w, h) {
            quoteP?.placeRelative(inset, inset)
            textP?.placeRelative(padL, textY)
            metaP.placeRelative(w - metaInsetR - metaP.width, metaY)
        }
        }
    }
}

/** Dark pill used for the time under big emoji and for date labels. */
@Composable
internal fun WaPill(label: String, modifier: Modifier = Modifier, tickStatus: String? = null) {
    val p = LocalWaPalette.current
    Row(
        modifier
            .clip(RoundedCornerShape(34.px))
            .background(p.pill)
            .padding(horizontal = 22.px, vertical = 18.px),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(label, style = WaText.time.copy(color = p.metaIncoming), maxLines = 1, softWrap = false)
        if (tickStatus != null) {
            Spacer(Modifier.width(12.px))
            WaTicks(tickStatus, if (tickStatus == "READ") p.tickRead else p.metaIncoming)
        }
    }
}

/** Round emoji badge that overlaps the bottom edge of a bubble. */
@Composable
internal fun WaReactionChip(reactions: List<String>, modifier: Modifier = Modifier) {
    val p = LocalWaPalette.current
    Row(
        modifier
            .height(72.px)
            .background(p.wallpaper, RoundedCornerShape(36.px))
            .padding(3.px)
            .background(p.incoming, RoundedCornerShape(33.px))
            .padding(horizontal = 11.px),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        reactions.take(3).forEach { emoji ->
            Box(Modifier.size(44.px), contentAlignment = Alignment.Center) {
                BasicText(emoji, style = WaText.reaction)
            }
        }
    }
}

internal fun isEmojiOnly(text: String): Boolean {
    val t = text.trim()
    if (t.isEmpty() || t.length > 24) return false
    var visible = 0
    var i = 0
    while (i < t.length) {
        val cp = t.codePointAt(i)
        i += Character.charCount(cp)
        if (cp == 0x200D || cp == 0xFE0F || cp in 0x1F3FB..0x1F3FF || Character.isWhitespace(cp)) continue
        val emojiLike = cp >= 0x1F000 || cp in 0x2190..0x2BFF || cp == 0x00A9 || cp == 0x00AE || cp == 0x203C || cp == 0x2049
        if (!emojiLike) return false
        visible++
    }
    return visible in 1..3
}

private val OverlayMetaColor = Color(0xFFE9EDEF)

/** Photo / video / voice / file block shown at the top of a bubble. */
@Composable
private fun WaMediaHeader(m: WaBubble, overlay: Boolean) {
    val p = LocalWaPalette.current
    when (m.kind) {
        "IMAGE", "VIDEO" -> Box(
            Modifier
                .width(676.px)
                .height(845.px)
                .clip(RoundedCornerShape(16.5.px))
                .background(p.quoteIncoming)
        ) {
            if (!m.attachmentUri.isNullOrBlank()) {
                AsyncImage(
                    model = m.attachmentUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }
            if (m.kind == "VIDEO") {
                Box(
                    Modifier.align(Alignment.Center).size(150.px).background(Color(0x99000000), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(96.px))
                }
            }
            if (overlay) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(150.px)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x88000000))))
                )
            }
        }
        "AUDIO" -> WaVoiceHeader(m)
        else -> Row(
            Modifier
                .width(676.px)
                .clip(RoundedCornerShape(16.5.px))
                .background(if (m.outgoing) p.quoteOutgoing else p.quoteIncoming)
                .padding(horizontal = 30.px, vertical = 28.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Description, contentDescription = null, tint = p.secondary, modifier = Modifier.size(72.px))
            Spacer(Modifier.width(24.px))
            BasicText(
                if (m.text.isBlank()) "Document" else m.text,
                style = WaText.message.copy(color = p.text),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Voice message: play button, a made-up waveform (seeded by the message id) and the duration label. */
@Composable
private fun WaVoiceHeader(m: WaBubble) {
    val p = LocalWaPalette.current
    val barColor = p.secondary
    val seed = m.id.hashCode()
    Row(
        Modifier.width(676.px).padding(top = 22.px, bottom = 6.px),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(26.px))
        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = p.secondary, modifier = Modifier.size(84.px))
        Spacer(Modifier.width(36.px))
        Column {
            Canvas(Modifier.width(480.px).height(66.px)) {
                val s = density / REFERENCE_DENSITY
                val barW = 4.5f * s
                val pitch = 11f * s
                var x = barW
                var i = 0
                val rnd = java.util.Random(seed.toLong())
                while (x < size.width - barW) {
                    val frac = if (i == 0) 0.35f else 0.18f + 0.82f * rnd.nextFloat()
                    val bh = size.height * frac
                    drawLine(
                        barColor,
                        Offset(x, (size.height - bh) / 2f),
                        Offset(x, (size.height + bh) / 2f),
                        strokeWidth = barW,
                        cap = StrokeCap.Round
                    )
                    x += pitch
                    i++
                }
                drawCircle(Color(0xFF53A6F5), radius = 15f * s, center = Offset(30f * s, size.height / 2f))
            }
            BasicText("0:00", style = WaText.time.copy(color = p.secondary))
        }
    }
}

/** One chat row: the bubble (or big emoji), its optional reaction badge, and the correct side margin and gap. */
@Composable
internal fun WaMessageRow(
    m: WaBubble,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    val p = LocalWaPalette.current
    val hasMedia = m.kind == "IMAGE" || m.kind == "VIDEO" || m.kind == "AUDIO" || m.kind == "FILE"
    val overlay = (m.kind == "IMAGE" || m.kind == "VIDEO") && m.text.isBlank() && m.quote == null
    val emojiOnly = !hasMedia && m.quote == null && isEmojiOnly(m.text)
    Box(
        modifier
            .fillMaxWidth()
            .padding(top = m.gapAbove.px)
            .padding(
                start = if (m.outgoing) 0.px else BUBBLE_SIDE_MARGIN.px,
                end = if (m.outgoing) BUBBLE_SIDE_MARGIN.px else 0.px
            ),
        contentAlignment = if (m.outgoing) Alignment.TopEnd else Alignment.TopStart
    ) {
        Column(horizontalAlignment = if (m.outgoing) Alignment.End else Alignment.Start) {
            Box(
                if (onLongClick != null) Modifier.pointerInput(onLongClick) { detectTapGestures(onLongPress = { onLongClick() }) } else Modifier
            ) {
                if (emojiOnly) {
                    Column(horizontalAlignment = if (m.outgoing) Alignment.End else Alignment.Start) {
                        BasicText(m.text.trim(), style = WaText.emojiLarge)
                        Spacer(Modifier.height(20.px))
                        WaPill(m.time, tickStatus = if (m.outgoing) m.status else null)
                    }
                } else {
                    val header: (@Composable (Modifier) -> Unit)? = if (m.quote != null || hasMedia) {
                        { mod: Modifier ->
                            Column(mod) {
                                if (m.quote != null) WaQuote(m.quote, m.outgoing, Modifier.fillMaxWidth())
                                if (m.quote != null && hasMedia) Spacer(Modifier.height(11.px))
                                if (hasMedia) WaMediaHeader(m, overlay)
                            }
                        }
                    } else null
                    Box(Modifier.background(if (m.outgoing) p.outgoing else p.incoming, WaBubbleShape(m.outgoing, m.tail))) {
                        WaTextBubbleBody(
                            text = if (m.kind == "FILE") "" else m.text,
                            outgoing = m.outgoing,
                            header = header,
                            overlayMeta = overlay,
                            meta = { mod ->
                                WaMeta(
                                    m.time, m.edited, m.outgoing, m.status, mod,
                                    colorOverride = if (overlay) OverlayMetaColor else null
                                )
                            }
                        )
                    }
                }
                if (m.reactions.isNotEmpty()) {
                    WaReactionChip(
                        m.reactions,
                        Modifier
                            .align(if (m.outgoing) Alignment.BottomEnd else Alignment.BottomStart)
                            .offset(x = if (m.outgoing) (-23.5).px else 23.5.px, y = 57.5.px)
                    )
                }
            }
            if (m.reactions.isNotEmpty()) Spacer(Modifier.height(66.px))
        }
    }
}
