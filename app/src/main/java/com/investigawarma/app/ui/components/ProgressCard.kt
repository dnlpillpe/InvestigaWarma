package com.investigawarma.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Muestra el progreso de una zona o de un nivel con barra + texto (nunca solo color). */
@Composable
fun ProgressCard(
    title: String,
    completed: Int,
    total: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text("$completed / $total", style = MaterialTheme.typography.titleMedium, color = accentColor)
        }
        Spacer(Modifier.height(8.dp))
        val progress = if (total > 0) completed.toFloat() / total else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = accentColor,
            trackColor = accentColor.copy(alpha = 0.15f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
        )
    }
}

@Composable
fun XpBar(levelName: String, progressPercent: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(levelName, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            Text("$progressPercent%", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progressPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
        )
    }
}
