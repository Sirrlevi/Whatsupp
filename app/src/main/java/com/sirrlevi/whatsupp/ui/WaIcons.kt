package com.sirrlevi.whatsupp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight

/*
 * The message-box icons, drawn from simple shapes (strokes, arcs, circles) sized to match the reference screenshot.
 * Every coordinate is a reference pixel measured on the 1080 px wide screenshot; IconFrame moves the origin to the
 * icon's own corner and scales to the real density.
 */
private class IconFrame(private val ox: Float, private val oy: Float, val s: Float) {
    fun x(v: Float) = (v - ox) * s
    fun y(v: Float) = (v - oy) * s
    fun o(vx: Float, vy: Float) = Offset(x(vx), y(vy))
}

private const val K = 0.5523f

/** Upright paperclip: outer loop, inner loop and a short free end. */
@Composable
internal fun WaPaperclipIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(36.px, 57.px)) {
        val f = IconFrame(552f, 2301.5f, size.width / 36f)
        val path = Path().apply {
            moveTo(f.x(584.5f), f.y(2315.85f))
            lineTo(f.x(584.5f), f.y(2340.4f))
            arcTo(Rect(f.x(555.5f), f.y(2325.9f), f.x(584.5f), f.y(2354.9f)), 0f, 180f, false)
            lineTo(f.x(555.5f), f.y(2314.5f))
            arcTo(Rect(f.x(555.5f), f.y(2304.7f), f.x(575.1f), f.y(2324.3f)), 180f, 180f, false)
            lineTo(f.x(575.1f), f.y(2339.2f))
            arcTo(Rect(f.x(564.95f), f.y(2334.15f), f.x(575.05f), f.y(2344.25f)), 0f, 180f, false)
            lineTo(f.x(565f), f.y(2315.85f))
        }
        drawPath(path, color, style = Stroke(5.4f * f.s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Rounded-square sticker with a smiling face and a peeled corner. */
@Composable
internal fun WaStickerIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(56.px, 57.px)) {
        val f = IconFrame(60f, 2301.5f, size.width / 56f)
        val stroke = Stroke(5.45f * f.s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val l = 63.25f
        val r = 112.7f
        val t = 2305.0f
        val b = 2354.6f
        val c = 17.4f
        val outline = Path().apply {
            moveTo(f.x(113.6f), f.y(2333f))
            lineTo(f.x(95.6f), f.y(b))
            lineTo(f.x(l + c), f.y(b))
            cubicTo(f.x(l + c - K * c), f.y(b), f.x(l), f.y(b - c + K * c), f.x(l), f.y(b - c))
            lineTo(f.x(l), f.y(t + c))
            cubicTo(f.x(l), f.y(t + c - K * c), f.x(l + c - K * c), f.y(t), f.x(l + c), f.y(t))
            lineTo(f.x(r - c), f.y(t))
            cubicTo(f.x(r - c + K * c), f.y(t), f.x(r), f.y(t + c - K * c), f.x(r), f.y(t + c))
            lineTo(f.x(r), f.y(2333f))
        }
        drawPath(outline, color, style = stroke)
        // fold line and the stem under the mouth
        drawLine(color, f.o(r, 2332.8f), f.o(93f, 2332.8f), stroke.width, StrokeCap.Round)
        drawLine(color, f.o(90.77f, 2334f), f.o(90.77f, 2354f), stroke.width, StrokeCap.Round)
        // eyes
        drawCircle(color, 4.5f * f.s, f.o(78.3f, 2320.8f))
        drawCircle(color, 4.5f * f.s, f.o(97.3f, 2320.8f))
        // open mouth
        val mouth = Path().apply {
            moveTo(f.x(71f), f.y(2333f))
            lineTo(f.x(90.8f), f.y(2333f))
            lineTo(f.x(90.8f), f.y(2343.5f))
            lineTo(f.x(88f), f.y(2343.5f))
            cubicTo(f.x(82f), f.y(2343.3f), f.x(74f), f.y(2339f), f.x(71f), f.y(2333f))
            close()
        }
        drawPath(mouth, color, style = Fill)
    }
}

/** Circle with a rupee sign (payments). */
@Composable
internal fun WaRupeeIcon(color: Color, modifier: Modifier = Modifier) {
    Box(modifier.size(56.px), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(56.px)) {
            val s = size.width / 56f
            drawCircle(color, radius = 25.05f * s, style = Stroke(5.4f * s))
        }
        BasicText(
            "₹",
            style = WaText.rupee.copy(color = color)
        )
    }
}

/** Camera: body with a raised viewfinder and a thick lens ring. */
@Composable
internal fun WaCameraIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(58.px, 52.px)) {
        val f = IconFrame(806f, 2304f, size.width / 58f)
        val stroke = Stroke(5.4f * f.s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val l = 809.8f
        val r = 859.8f
        val t = 2313.2f
        val b = 2351.7f
        val c = 5f
        val body = Path().apply {
            moveTo(f.x(822.8f), f.y(t))
            lineTo(f.x(l + c), f.y(t))
            quadraticTo(f.x(l), f.y(t), f.x(l), f.y(t + c))
            lineTo(f.x(l), f.y(b - c))
            quadraticTo(f.x(l), f.y(b), f.x(l + c), f.y(b))
            lineTo(f.x(r - c), f.y(b))
            quadraticTo(f.x(r), f.y(b), f.x(r), f.y(b - c))
            lineTo(f.x(r), f.y(t + c))
            quadraticTo(f.x(r), f.y(t), f.x(r - c), f.y(t))
            lineTo(f.x(846.8f), f.y(t))
            lineTo(f.x(840.8f), f.y(2307.9f))
            lineTo(f.x(828.8f), f.y(2307.9f))
            close()
        }
        drawPath(body, color, style = stroke)
        drawCircle(color, 10.05f * f.s, f.o(834.8f, 2332.7f), style = stroke)
    }
}
