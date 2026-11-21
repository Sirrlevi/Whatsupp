package com.sirrlevi.whatsupp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Height of the chat toolbar below the status bar, in reference pixels. */
internal const val TOP_BAR_HEIGHT = 154f

@Composable
private fun BoxScope.BarIcon(centerX: Float, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier
            .align(Alignment.CenterStart)
            .offset(x = (centerX - 66f).px)
            .size(132.px)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

/** The video-call glyph: rounded outline with a small solid lens on the right. */
@Composable
internal fun WaVideoGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(56.px, 44.px)) {
        val s = size.width / 56f
        drawRoundRect(
            color,
            topLeft = Offset(3f * s, 3f * s),
            size = Size(39f * s, 38f * s),
            cornerRadius = CornerRadius(5f * s, 5f * s),
            style = Stroke(width = 6f * s)
        )
        val lens = Path().apply {
            moveTo(45.5f * s, 21.5f * s)
            lineTo(54f * s, 10.3f * s)
            lineTo(54f * s, 32.7f * s)
            close()
        }
        drawPath(lens, color, style = Fill)
        drawPath(lens, color, style = Stroke(width = 3f * s, join = StrokeJoin.Round))
    }
}

/** Chat toolbar: back arrow, round picture, name (and optional status line), video, call and the three-dot menu. */
@Composable
internal fun WaTopBar(
    name: String,
    subtitle: String?,
    onBack: () -> Unit,
    onProfile: () -> Unit,
    onVideo: () -> Unit,
    onCall: () -> Unit,
    onMore: () -> Unit,
    avatar: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {}
) {
    val p = LocalWaPalette.current
    Box(modifier.fillMaxWidth().height(TOP_BAR_HEIGHT.px).background(p.topBar)) {
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .offset(x = 120.px)
                .width(600.px)
                .height(TOP_BAR_HEIGHT.px)
                .clickable(onClick = onProfile)
        )
        BarIcon(65.5f, onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = p.icon, modifier = Modifier.size(66.px))
        }
        avatar(Modifier.align(Alignment.CenterStart).offset(x = 143.px).size(99.px).clip(CircleShape))
        Column(
            Modifier.align(Alignment.CenterStart).offset(x = 274.8.px).width(440.px)
        ) {
            BasicText(
                name,
                style = WaText.topBarName.copy(color = p.text),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                BasicText(
                    subtitle,
                    style = WaText.topBarStatus.copy(color = p.secondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        BarIcon(770.5f, onVideo) { WaVideoGlyph(p.icon) }
        BarIcon(902.5f, onCall) {
            Icon(Icons.Outlined.Call, contentDescription = "Call", tint = p.icon, modifier = Modifier.size(66.px))
        }
        BarIcon(1018.5f, onMore) {
            Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = p.icon, modifier = Modifier.size(66.px))
        }
        Box(Modifier.align(Alignment.TopEnd)) { menu() }
    }
}

/**
 * Message box: a rounded pill with the emoji button, the text field, paperclip, rupee and camera icons, and the
 * round green button that is a microphone while the box is empty and a send arrow while there is text.
 */
@Composable
internal fun WaInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onEmoji: () -> Unit,
    onAttach: () -> Unit,
    onCamera: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    attachMenu: @Composable () -> Unit = {}
) {
    val p = LocalWaPalette.current
    val empty = value.isEmpty()
    Box(modifier.fillMaxWidth().height(145.px)) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .offset(x = 21.px, y = 0.px)
                .width(878.5f.px)
                .height(134.px)
                .background(p.pill, RoundedCornerShape(67.px))
        ) {
            Box(
                Modifier.align(Alignment.CenterStart).offset(x = (88f - 21f - 33f).px).size(66.px).clip(CircleShape).clickable(onClick = onEmoji),
                contentAlignment = Alignment.Center
            ) {
                WaStickerIcon(p.secondary)
            }
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (155.5f - 21f).px)
                    .width((if (empty) 330f else 560f).px)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = WaText.input.copy(color = p.text),
                    cursorBrush = SolidColor(p.green),
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (empty) BasicText("Message", style = WaText.input.copy(color = p.secondary))
                            inner()
                        }
                    }
                )
            }
            Box(
                Modifier.align(Alignment.CenterStart).offset(x = (570f - 21f - 33f).px).size(66.px).clip(CircleShape).clickable(onClick = onAttach),
                contentAlignment = Alignment.Center
            ) {
                attachMenu()
                WaPaperclipIcon(p.secondary)
            }
            if (empty) {
                Box(
                    Modifier.align(Alignment.CenterStart).offset(x = (702.75f - 21f - 33f).px).size(66.px),
                    contentAlignment = Alignment.Center
                ) { WaRupeeIcon(p.secondary) }
                Box(
                    Modifier.align(Alignment.CenterStart).offset(x = (834.8f - 21f - 33f).px).size(66.px).clip(CircleShape).clickable(onClick = onCamera),
                    contentAlignment = Alignment.Center
                ) { WaCameraIcon(p.secondary) }
            }
        }
        Box(
            Modifier
                .align(Alignment.TopStart)
                .offset(x = 921.px, y = 1.px)
                .size(132.px)
                .clip(CircleShape)
                .background(p.green)
                .clickable(onClick = onAction),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (empty) Icons.Filled.Mic else Icons.AutoMirrored.Filled.Send,
                contentDescription = if (empty) "Voice message" else "Send",
                tint = p.onGreen,
                modifier = Modifier.size(66.px)
            )
        }
    }
}

/** Round "jump to newest" button with the unread badge. */
@Composable
internal fun WaScrollFab(unread: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = LocalWaPalette.current
    Box(modifier.size(121.px, 116.px)) {
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(88.px)
                .shadow(6.px, CircleShape)
                .clip(CircleShape)
                .background(p.fab)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.KeyboardDoubleArrowDown, contentDescription = "Scroll to latest", tint = p.fabIcon, modifier = Modifier.size(60.px))
        }
        if (unread > 0) {
            Box(
                Modifier.align(Alignment.TopStart).size(56.px).background(p.green, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                BasicText(unread.toString(), style = WaText.time.copy(color = p.onGreen))
            }
        }
    }
}

/** Date label between days of messages. */
@Composable
internal fun WaDateChip(label: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(68.px), contentAlignment = Alignment.Center) {
        WaPill(label)
    }
}

/**
 * Original doodle wallpaper (not WhatsApp's artwork): faint outline icons on a dark or light base.
 * The pattern is deterministic so it never shifts between frames.
 */
@Composable
internal fun WaDoodleWallpaper(modifier: Modifier = Modifier) {
    val p = LocalWaPalette.current
    Canvas(modifier.fillMaxSize().background(p.wallpaper)) {
        val s = density / REFERENCE_DENSITY
        val cell = 230f * s
        val stroke = Stroke(width = 3.2f * s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        var row = 0
        var y = 40f * s
        while (y < size.height + cell) {
            var col = 0
            var x = (if (row % 2 == 0) 40f else 155f) * s
            while (x < size.width + cell) {
                val h = (row * 7919 + col * 104729 + 13) % 997
                val jx = ((h % 61) - 30) * s
                val jy = (((h / 7) % 61) - 30) * s
                drawDoodle(h % 10, Offset(x + jx, y + jy), 46f * s, p.doodle, stroke)
                x += cell
                col++
            }
            y += cell * 0.62f
            row++
        }
    }
}

private fun DrawScope.drawDoodle(kind: Int, c: Offset, r: Float, color: Color, stroke: Stroke) {
    when (kind) {
        0 -> drawCircle(color, r * 0.8f, c, style = stroke)
        1 -> drawRoundRect(color, Offset(c.x - r * 1.1f, c.y - r * 0.55f), Size(r * 2.2f, r * 1.1f), CornerRadius(r * 0.55f), style = stroke)
        2 -> { // sparkle
            val p = Path()
            p.moveTo(c.x, c.y - r)
            p.quadraticBezierTo(c.x, c.y, c.x + r, c.y)
            p.quadraticBezierTo(c.x, c.y, c.x, c.y + r)
            p.quadraticBezierTo(c.x, c.y, c.x - r, c.y)
            p.quadraticBezierTo(c.x, c.y, c.x, c.y - r)
            drawPath(p, color, style = stroke)
        }
        3 -> { // heart
            val p = Path()
            p.moveTo(c.x, c.y + r * 0.9f)
            p.cubicTo(c.x - r * 1.6f, c.y - r * 0.2f, c.x - r * 0.7f, c.y - r * 1.2f, c.x, c.y - r * 0.4f)
            p.cubicTo(c.x + r * 0.7f, c.y - r * 1.2f, c.x + r * 1.6f, c.y - r * 0.2f, c.x, c.y + r * 0.9f)
            drawPath(p, color, style = stroke)
        }
        4 -> { // speech bubble
            drawRoundRect(color, Offset(c.x - r, c.y - r * 0.7f), Size(r * 2f, r * 1.4f), CornerRadius(r * 0.4f), style = stroke)
            val p = Path()
            p.moveTo(c.x - r * 0.4f, c.y + r * 0.7f)
            p.lineTo(c.x - r * 0.7f, c.y + r * 1.15f)
            p.lineTo(c.x, c.y + r * 0.7f)
            drawPath(p, color, style = stroke)
        }
        5 -> { // crescent
            val p = Path()
            p.addArc(androidx.compose.ui.geometry.Rect(c.x - r, c.y - r, c.x + r, c.y + r), 60f, 240f)
            p.addArc(androidx.compose.ui.geometry.Rect(c.x - r * 0.45f, c.y - r * 0.85f, c.x + r * 1.15f, c.y + r * 0.75f), 290f, -220f)
            drawPath(p, color, style = stroke)
        }
        6 -> { // triangle
            val p = Path()
            p.moveTo(c.x, c.y - r)
            p.lineTo(c.x + r * 0.95f, c.y + r * 0.7f)
            p.lineTo(c.x - r * 0.95f, c.y + r * 0.7f)
            p.close()
            drawPath(p, color, style = stroke)
        }
        7 -> { // lightning
            val p = Path()
            p.moveTo(c.x + r * 0.3f, c.y - r)
            p.lineTo(c.x - r * 0.6f, c.y + r * 0.1f)
            p.lineTo(c.x + r * 0.1f, c.y + r * 0.1f)
            p.lineTo(c.x - r * 0.3f, c.y + r)
            drawPath(p, color, style = stroke)
        }
        8 -> { // flower
            for (i in 0 until 5) {
                val a = (i * 72 - 90) * PI / 180.0
                drawCircle(color, r * 0.38f, Offset(c.x + (cos(a) * r * 0.62f).toFloat(), c.y + (sin(a) * r * 0.62f).toFloat()), style = stroke)
            }
        }
        else -> { // plus
            drawLine(color, Offset(c.x - r * 0.8f, c.y), Offset(c.x + r * 0.8f, c.y), stroke.width, StrokeCap.Round)
            drawLine(color, Offset(c.x, c.y - r * 0.8f), Offset(c.x, c.y + r * 0.8f), stroke.width, StrokeCap.Round)
        }
    }
}
