package com.investigawarma.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val AVATAR_COLORS = listOf(
    Color(0xFF5B3FE0), Color(0xFF20D3C2), Color(0xFFFFB020), Color(0xFFFF6B6B),
    Color(0xFF2ECC71), Color(0xFF3FA7D6), Color(0xFF9B5DE5), Color(0xFFE0637A),
)

/** 8 avatares locales, distinguibles por color y símbolo. No usan fotos ni datos personales. */
@Composable
fun AvatarIcon(avatarId: Int, modifier: Modifier = Modifier.size(56.dp), selected: Boolean = false) {
    val idx = avatarId.coerceIn(0, 7)
    val color = AVATAR_COLORS[idx]
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(color.copy(alpha = if (selected) 0.35f else 0.2f), radius = w / 2f, center = Offset(w / 2f, h / 2f))
        if (selected) {
            drawCircle(color, radius = w / 2f - 2, center = Offset(w / 2f, h / 2f), style = Stroke(width = w * 0.05f))
        }
        val cx = w / 2f
        val cy = h / 2f
        when (idx) {
            0 -> { // estrella
                val path = androidx.compose.ui.graphics.Path()
                for (i in 0 until 10) {
                    val angle = -PI / 2 + i * PI / 5
                    val r = if (i % 2 == 0) w * 0.28f else w * 0.12f
                    val x = cx + (r * cos(angle)).toFloat()
                    val y = cy + (r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, color)
            }
            1 -> drawCircle(color, radius = w * 0.22f, center = Offset(cx, cy), style = Stroke(width = w * 0.05f)) // molécula/anillo
            2 -> { // rayo
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx + w * 0.05f, cy - h * 0.28f)
                    lineTo(cx - w * 0.15f, cy + h * 0.05f)
                    lineTo(cx, cy + h * 0.05f)
                    lineTo(cx - w * 0.05f, cy + h * 0.28f)
                    lineTo(cx + w * 0.15f, cy - h * 0.05f)
                    lineTo(cx, cy - h * 0.05f)
                    close()
                }
                drawPath(path, color)
            }
            3 -> drawCircle(color, radius = w * 0.24f, center = Offset(cx, cy)) // planeta simple
            4 -> { // hoja
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx, cy - h * 0.26f)
                    cubicTo(cx + w * 0.26f, cy - h * 0.1f, cx + w * 0.2f, cy + h * 0.26f, cx, cy + h * 0.28f)
                    cubicTo(cx - w * 0.2f, cy + h * 0.26f, cx - w * 0.26f, cy - h * 0.1f, cx, cy - h * 0.26f)
                    close()
                }
                drawPath(path, color)
            }
            5 -> { // brújula
                drawCircle(color, radius = w * 0.26f, center = Offset(cx, cy), style = Stroke(width = w * 0.04f))
                drawLine(color, Offset(cx, cy - h * 0.2f), Offset(cx, cy + h * 0.2f), strokeWidth = w * 0.03f, cap = StrokeCap.Round)
            }
            6 -> { // matraz
                drawLine(color, Offset(cx - w * 0.06f, cy - h * 0.24f), Offset(cx - w * 0.16f, cy + h * 0.2f), strokeWidth = w * 0.04f)
                drawLine(color, Offset(cx + w * 0.06f, cy - h * 0.24f), Offset(cx + w * 0.16f, cy + h * 0.2f), strokeWidth = w * 0.04f)
                drawLine(color, Offset(cx - w * 0.16f, cy + h * 0.2f), Offset(cx + w * 0.16f, cy + h * 0.2f), strokeWidth = w * 0.04f)
            }
            else -> { // cohete
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx, cy - h * 0.28f)
                    lineTo(cx + w * 0.12f, cy + h * 0.1f)
                    lineTo(cx - w * 0.12f, cy + h * 0.1f)
                    close()
                }
                drawPath(path, color)
                drawCircle(color, radius = w * 0.1f, center = Offset(cx, cy + h * 0.18f))
            }
        }
    }
}
