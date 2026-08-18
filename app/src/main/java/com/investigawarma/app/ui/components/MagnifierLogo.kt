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

/**
 * Logotipo InvestigaWarma: la lupa del joven investigador, con una estrella
 * de descubrimiento dentro del cristal. Mismo concepto que el icono de la app.
 */
@Composable
fun MagnifierLogo(modifier: Modifier = Modifier.size(120.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cyan = Color(0xFF20D3C2)
        val amber = Color(0xFFFFB020)
        val coral = Color(0xFFFF6B6B)
        val white = Color.White

        // Mango
        drawLine(amber, Offset(w * 0.65f, h * 0.65f), Offset(w * 0.85f, h * 0.85f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
        // Aro
        drawCircle(cyan, radius = w * 0.32f, center = Offset(w * 0.45f, h * 0.45f), style = Stroke(width = w * 0.045f))

        // Estrella central
        val cx = w * 0.45f
        val cy = h * 0.45f
        val outerR = w * 0.14f
        val innerR = outerR * 0.45f
        val path = androidx.compose.ui.graphics.Path()
        for (i in 0 until 10) {
            val angle = -PI / 2 + i * PI / 5
            val r = if (i % 2 == 0) outerR else innerR
            val x = cx + (r * cos(angle)).toFloat()
            val y = cy + (r * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, white)

        // Partículas
        drawCircle(coral, radius = w * 0.02f, center = Offset(w * 0.18f, h * 0.22f))
        drawCircle(cyan, radius = w * 0.025f, center = Offset(w * 0.82f, h * 0.28f))
        drawCircle(amber, radius = w * 0.018f, center = Offset(w * 0.85f, h * 0.55f))
    }
}
