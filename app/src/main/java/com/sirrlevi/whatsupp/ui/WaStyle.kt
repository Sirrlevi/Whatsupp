package com.sirrlevi.whatsupp.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Every size in the chat UI is written in "reference pixels": the pixels of a 1080 px wide screenshot taken at
 * 2.75 px per dp. The app overrides its density so that one reference pixel is exactly one real pixel on a
 * 1080 px wide phone (and scales with the screen width on other phones). Text uses the same scale with the
 * system font scale switched off, so bubbles line up with the reference screenshots.
 */
internal const val REFERENCE_DENSITY = 2.75f
internal const val REFERENCE_WIDTH_DP = 1080f / REFERENCE_DENSITY

internal val Number.px: Dp get() = (toFloat() / REFERENCE_DENSITY).dp
internal val Number.spx: TextUnit get() = (toFloat() / REFERENCE_DENSITY).sp

/**
 * Switches the subtree to the reference scale: one reference pixel is one real pixel on a 1080 px wide window
 * (proportionally scaled on other widths), and the system font size setting is ignored so text wraps the same way.
 */
@Composable
internal fun WaDensityRoot(content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val density = remember(widthPx) { Density(widthPx / REFERENCE_WIDTH_DP, 1f) }
        CompositionLocalProvider(LocalDensity provides density) { content() }
    }
}

/** Colours measured from the reference screenshots (dark theme) plus a matching light theme. */
@Immutable
internal class WaPalette(
    val dark: Boolean,
    val topBar: Color,
    val wallpaper: Color,
    val doodle: Color,
    val incoming: Color,
    val outgoing: Color,
    val quoteIncoming: Color,
    val quoteOutgoing: Color,
    val text: Color,
    val secondary: Color,
    val metaIncoming: Color,
    val metaOutgoing: Color,
    val quoteTextOutgoing: Color,
    val green: Color,
    val purple: Color,
    val titleMe: Color,
    val titleThem: Color,
    val tickRead: Color,
    val pill: Color,
    val chip: Color,
    val fab: Color,
    val fabIcon: Color,
    val onGreen: Color,
    val divider: Color,
    val icon: Color
)

internal object WaPalettes {
    val Dark = WaPalette(
        dark = true,
        topBar = Color(0xFF0B1014),
        wallpaper = Color(0xFF030708),
        doodle = Color(0xFF12181B),
        incoming = Color(0xFF1F262A),
        outgoing = Color(0xFF134D37),
        quoteIncoming = Color(0xFF1C2329),
        quoteOutgoing = Color(0xFF124333),
        text = Color(0xFFF6F8FA),
        secondary = Color(0xFF8D9598),
        metaIncoming = Color(0xFFA4A8AB),
        metaOutgoing = Color(0xFF9DBBAF),
        quoteTextOutgoing = Color(0xFF9EB7AF),
        green = Color(0xFF21C063),
        purple = Color(0xFFA791FE),
        titleMe = Color(0xFFABE3C3),
        titleThem = Color(0xFFD6D6FB),
        tickRead = Color(0xFF53BDEB),
        pill = Color(0xFF1F272A),
        chip = Color(0xFF23282C),
        fab = Color(0xFF2A343C),
        fabIcon = Color(0xFF9AA3A8),
        onGreen = Color(0xFF0B1014),
        divider = Color(0xFF1B2226),
        icon = Color(0xFFF6F8FA)
    )

    val Light = WaPalette(
        dark = false,
        topBar = Color(0xFFFFFFFF),
        wallpaper = Color(0xFFEFEAE2),
        doodle = Color(0xFFE2DCD1),
        incoming = Color(0xFFFFFFFF),
        outgoing = Color(0xFFD9FDD3),
        quoteIncoming = Color(0xFFF2F4F5),
        quoteOutgoing = Color(0xFFCFF0C9),
        text = Color(0xFF111B21),
        secondary = Color(0xFF667781),
        metaIncoming = Color(0xFF667781),
        metaOutgoing = Color(0xFF5E8F6A),
        quoteTextOutgoing = Color(0xFF4E6F58),
        green = Color(0xFF1DAA61),
        purple = Color(0xFF7F66FF),
        titleMe = Color(0xFF1E8E5A),
        titleThem = Color(0xFF6A55D8),
        tickRead = Color(0xFF53BDEB),
        pill = Color(0xFFFFFFFF),
        chip = Color(0xFFF0F2F5),
        fab = Color(0xFFFFFFFF),
        fabIcon = Color(0xFF667781),
        onGreen = Color(0xFFFFFFFF),
        divider = Color(0xFFE9EDEF),
        icon = Color(0xFF111B21)
    )
}

internal val LocalWaPalette = staticCompositionLocalOf { WaPalettes.Dark }

/** Text styles with every field set explicitly, so nothing is inherited from Material (letter spacing, line height). */
internal object WaText {
    private val lineStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Proportional,
        trim = LineHeightStyle.Trim.None
    )

    private fun style(sizePx: Float, lineHeightPx: Float, weight: FontWeight = FontWeight.Normal) = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = weight,
        fontSize = sizePx.spx,
        lineHeight = lineHeightPx.spx,
        letterSpacing = 0.sp,
        lineHeightStyle = lineStyle,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    val message = style(36.5f, 44f)
    val quoteTitle = style(33f, 45f, FontWeight.Medium)
    val quoteBody = style(33f, 45f)
    val time = style(27.25f, 32f)
    val topBarName = style(38.5f, 46f)
    val topBarStatus = style(29f, 36f)
    val input = style(41f, 48f)
    val rupee = style(36f, 44f, FontWeight.Medium)
    val emojiLarge = style(102f, 120f)
    val reaction = style(36f, 44f)
    val dateChip = style(29f, 36f)
}
