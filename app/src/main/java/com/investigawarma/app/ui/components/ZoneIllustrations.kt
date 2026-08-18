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
import com.investigawarma.app.domain.model.Zone
import kotlin.math.cos
import kotlin.math.sin

/**
 * Iconografía propia por zona, dibujada con Compose Canvas (sin depender de
 * imágenes externas): cada zona tiene un símbolo visual distinto y reconocible.
 */
@Composable
fun ZoneIcon(zone: Zone, color: Color, modifier: Modifier = Modifier.size(48.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        when (zone) {
            Zone.SALA_OBSERVACION -> {
                // Lupa
                drawCircle(color, radius = w * 0.28f, center = Offset(w * 0.42f, h * 0.42f), style = Stroke(width = w * 0.08f))
                drawLine(color, Offset(w * 0.62f, h * 0.62f), Offset(w * 0.88f, h * 0.88f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
            }
            Zone.LABORATORIO_EXPERIMENTAL -> {
                // Matraz
                val neckW = w * 0.14f
                drawLine(color, Offset(w * 0.5f - neckW / 2, h * 0.15f), Offset(w * 0.5f - neckW / 2, h * 0.45f), strokeWidth = w * 0.07f)
                drawLine(color, Offset(w * 0.5f + neckW / 2, h * 0.15f), Offset(w * 0.5f + neckW / 2, h * 0.45f), strokeWidth = w * 0.07f)
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f - neckW / 2, h * 0.45f)
                    lineTo(w * 0.15f, h * 0.85f)
                    lineTo(w * 0.85f, h * 0.85f)
                    lineTo(w * 0.5f + neckW / 2, h * 0.45f)
                    close()
                }
                drawPath(path, color, style = Stroke(width = w * 0.06f))
            }
            Zone.BIODESCUBRIMIENTO -> {
                // Hoja
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f, h * 0.1f)
                    cubicTo(w * 0.95f, h * 0.35f, w * 0.85f, h * 0.85f, w * 0.5f, h * 0.9f)
                    cubicTo(w * 0.15f, h * 0.85f, w * 0.05f, h * 0.35f, w * 0.5f, h * 0.1f)
                    close()
                }
                drawPath(path, color, style = Stroke(width = w * 0.06f))
                drawLine(color, Offset(w * 0.5f, h * 0.15f), Offset(w * 0.5f, h * 0.85f), strokeWidth = w * 0.04f)
            }
            Zone.PLANETA_TIERRA -> {
                drawCircle(color, radius = w * 0.35f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.06f))
                drawOval(
                    color = color,
                    topLeft = Offset(w * 0.15f, h * 0.4f),
                    size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.2f),
                    style = Stroke(width = w * 0.045f),
                )
                drawLine(color, Offset(w * 0.5f, h * 0.15f), Offset(w * 0.5f, h * 0.85f), strokeWidth = w * 0.04f)
            }
            Zone.CENTRO_DE_DATOS -> {
                // Barras de gráfico
                val barWidth = w * 0.16f
                val bars = listOf(0.4f, 0.7f, 0.5f, 0.85f)
                bars.forEachIndexed { i, hf ->
                    val x = w * 0.12f + i * (barWidth + w * 0.06f)
                    drawLine(
                        color,
                        Offset(x, h * 0.9f),
                        Offset(x, h * (0.9f - hf * 0.7f)),
                        strokeWidth = barWidth,
                        cap = StrokeCap.Round,
                    )
                }
            }
            Zone.MUSEO_CIENTIFICO -> {
                // Trofeo/copa
                drawArc(
                    color = color,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.2f, h * 0.15f),
                    size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.5f),
                    style = Stroke(width = w * 0.06f),
                )
                drawLine(color, Offset(w * 0.5f, h * 0.55f), Offset(w * 0.5f, h * 0.75f), strokeWidth = w * 0.06f)
                drawLine(color, Offset(w * 0.3f, h * 0.85f), Offset(w * 0.7f, h * 0.85f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
            }
        }
    }
}

/** Estrella dibujada a mano (no depende de un icon font) para el sistema de recompensas. */
@Composable
fun StarShape(filled: Boolean, color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val outerR = w / 2f
        val innerR = outerR * 0.45f
        val path = androidx.compose.ui.graphics.Path()
        for (i in 0 until 10) {
            val angle = Math.PI / 2 * -1 + i * Math.PI / 5
            val r = if (i % 2 == 0) outerR else innerR
            val x = cx + (r * cos(angle)).toFloat()
            val y = cy + (r * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        if (filled) {
            drawPath(path, color)
        } else {
            drawPath(path, color, style = Stroke(width = w * 0.06f))
        }
    }
}

@Composable
fun StarRow(count: Int, total: Int = 3, modifier: Modifier = Modifier, color: Color = Color(0xFFFFB020)) {
    androidx.compose.foundation.layout.Row(modifier = modifier) {
        repeat(total) { i ->
            StarShape(filled = i < count, color = color, modifier = Modifier.size(20.dp))
            androidx.compose.foundation.layout.Spacer(Modifier.size(2.dp))
        }
    }
}
