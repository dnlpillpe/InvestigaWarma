package com.investigawarma.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

enum class IrisExpression { FELIZ, SORPRENDIDA, INVESTIGANDO, CELEBRANDO, EXPLICANDO }

/**
 * IRIS: la guía científica de la Academia. Dibujada íntegramente con Canvas
 * (identidad visual propia de InvestigaWarma, sin imágenes externas).
 * Sus mensajes son siempre cortos, según la especificación del personaje.
 */
@Composable
fun IrisAvatar(expression: IrisExpression, modifier: Modifier = Modifier.size(64.dp)) {
    Canvas(
        modifier = modifier.semantics { contentDescription = "IRIS, tu guía científica, $expression" },
    ) {
        val w = size.width
        val h = size.height
        val skin = Color(0xFFF3C7A0)
        val hair = Color(0xFF2E1A47)
        val coat = Color(0xFFFFFFFF)

        // Cabello (fondo)
        drawCircle(hair, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.38f))
        // Cara
        drawCircle(skin, radius = w * 0.33f, center = Offset(w * 0.5f, h * 0.42f))
        // Flequillo lateral
        drawCircle(hair, radius = w * 0.12f, center = Offset(w * 0.22f, h * 0.3f))
        drawCircle(hair, radius = w * 0.1f, center = Offset(w * 0.78f, h * 0.3f))
        // Bata / cuello
        drawRect(
            coat,
            topLeft = Offset(w * 0.25f, h * 0.75f),
            size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.25f),
        )

        val eyeY = when (expression) {
            IrisExpression.SORPRENDIDA -> h * 0.4f
            else -> h * 0.42f
        }
        val eyeR = when (expression) {
            IrisExpression.SORPRENDIDA -> w * 0.045f
            else -> w * 0.03f
        }
        drawCircle(hair, radius = eyeR, center = Offset(w * 0.4f, eyeY))
        drawCircle(hair, radius = eyeR, center = Offset(w * 0.6f, eyeY))

        // Boca según expresión
        val mouthPath = androidx.compose.ui.graphics.Path()
        when (expression) {
            IrisExpression.FELIZ, IrisExpression.CELEBRANDO -> {
                mouthPath.moveTo(w * 0.4f, h * 0.52f)
                mouthPath.quadraticTo(w * 0.5f, h * 0.6f, w * 0.6f, h * 0.52f)
            }
            IrisExpression.SORPRENDIDA -> {
                drawCircle(hair, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.54f))
            }
            IrisExpression.INVESTIGANDO, IrisExpression.EXPLICANDO -> {
                mouthPath.moveTo(w * 0.42f, h * 0.54f)
                mouthPath.lineTo(w * 0.58f, h * 0.54f)
            }
        }
        drawPath(mouthPath, hair, style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.025f))
    }
}

/** Burbuja de diálogo corta de IRIS, usada para misiones, pistas y celebraciones. */
@Composable
fun IrisMessageBubble(
    message: String,
    modifier: Modifier = Modifier,
    expression: IrisExpression = IrisExpression.FELIZ,
) {
    Row(modifier = modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        IrisAvatar(expression = expression, modifier = Modifier.size(56.dp))
        Spacer(Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
        ) {
            Text(
                message,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
