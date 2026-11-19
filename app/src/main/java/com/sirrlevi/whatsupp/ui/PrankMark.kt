package com.sirrlevi.whatsupp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The labels that mark every screen of this app as a prank / chat generator.
 * They are constants on purpose: there is no setting, wallpaper or theme that can switch them off.
 */
object PrankLabels {
    const val WATERMARK = "PRANK"
    const val HEADER = "PRANK APK • CHAT GENERATOR • PRANK FUN"
    const val FOOTER = "PRANK APK CHAT GENERATOR • PRANK FUN"

    const val WATERMARK_ROTATION_DEGREES = -24f
    const val WATERMARK_SIZE_SP = 88
    const val WATERMARK_FILL_ALPHA = 0.30f
    const val WATERMARK_OUTLINE_ALPHA = 0.38f

    val all: List<String> = listOf(WATERMARK, HEADER, FOOTER)
}

/**
 * Wraps the whole app: a header label on top, a footer label at the bottom and a large "PRANK" watermark
 * drawn above whatever screen, wallpaper or message list is showing in between.
 * The watermark is a separate layer, so changing the chat wallpaper or theme never removes it.
 */
@Composable
fun PrankFrame(isDark: Boolean, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        PrankBanner(PrankLabels.HEADER, isDark)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            content()
            PrankWatermark()
        }
        PrankBanner(PrankLabels.FOOTER, isDark)
    }
}

@Composable
private fun PrankBanner(text: String, isDark: Boolean) {
    val background = if (isDark) Color(0xFF1F2C34) else Color(0xFFFFF3CD)
    val foreground = if (isDark) Color(0xFFFFD54F) else Color(0xFF7A4B00)
    Box(
        Modifier.fillMaxWidth().background(background).padding(vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = foreground,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

/** Large diagonal "PRANK" in the middle of the screen. It never receives touches, so the chat stays usable. */
@Composable
fun PrankWatermark() {
    val base = TextStyle(
        fontSize = PrankLabels.WATERMARK_SIZE_SP.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 4.sp
    )
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .wrapContentSize(Alignment.Center, unbounded = true)
                .rotate(PrankLabels.WATERMARK_ROTATION_DEGREES)
        ) {
            // Dark outline underneath, white fill on top: readable on light, dark and photo wallpapers alike.
            Text(
                text = PrankLabels.WATERMARK,
                style = base.copy(
                    color = Color.Black.copy(alpha = PrankLabels.WATERMARK_OUTLINE_ALPHA),
                    drawStyle = Stroke(width = 6f, join = StrokeJoin.Round)
                ),
                softWrap = false,
                maxLines = 1
            )
            Text(
                text = PrankLabels.WATERMARK,
                style = base.copy(color = Color.White.copy(alpha = PrankLabels.WATERMARK_FILL_ALPHA)),
                softWrap = false,
                maxLines = 1
            )
        }
    }
}
