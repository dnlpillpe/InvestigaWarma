package com.investigawarma.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.SectionHeader
import com.investigawarma.app.ui.theme.DiscoveryCyan
import com.investigawarma.app.ui.theme.LabViolet
import com.investigawarma.app.ui.viewmodel.StatsViewModel
import com.investigawarma.app.ui.viewmodel.ViewModelFactory

/**
 * Centro de Datos: estadísticas calculadas SIEMPRE desde datos persistidos
 * (misiones completadas, precisión en desafíos, hipótesis válidas, etc.),
 * con un gráfico de barras simple dibujado con Compose Canvas.
 */
@Composable
fun StatsScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: StatsViewModel = viewModel(factory = factory)
    val stats by viewModel.stats.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás") }
            Text("Centro de Datos", style = MaterialTheme.typography.titleLarge)
        }

        val s = stats ?: return@Column

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            item {
                SectionHeader(title = "Tu progreso", subtitle = "${s.completedMissions} de ${s.totalMissions} misiones completadas")
                AppCard {
                    Text("Progreso por zona", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    ZoneBarChart(s.zoneStats)
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                AppCard {
                    StatRow("Precisión en desafíos", "${s.challengeAccuracyPercent}% (${s.successfulChallengeAttempts}/${s.totalChallengeAttempts})")
                    StatRow("Experimentos realizados", "${s.totalExperiments}")
                    StatRow("Hipótesis válidas", "${s.validHypotheses}/${s.totalHypotheses}")
                    StatRow("Insignias desbloqueadas", "${s.unlockedBadges}/${s.totalBadges}")
                    StatRow("Objetos del museo", "${s.unlockedCollectionItems}/${s.totalCollectionItems}")
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.titleMedium, color = LabViolet)
    }
}

@Composable
private fun ZoneBarChart(zoneStats: List<com.investigawarma.app.data.repository.StatsRepository.ZoneStat>) {
    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
        if (zoneStats.isEmpty()) return@Canvas
        val barSlot = size.width / zoneStats.size
        val barWidth = barSlot * 0.45f
        zoneStats.forEachIndexed { i, zs ->
            val ratio = if (zs.total > 0) zs.completed.toFloat() / zs.total else 0f
            val barHeight = size.height * 0.85f * ratio
            val x = barSlot * i + barSlot / 2f
            drawLine(
                color = DiscoveryCyan,
                start = Offset(x, size.height),
                end = Offset(x, size.height - barHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}
