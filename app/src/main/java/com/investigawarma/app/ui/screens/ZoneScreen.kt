package com.investigawarma.app.ui.screens

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.domain.model.Zone
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.EmptyState
import com.investigawarma.app.ui.components.SectionHeader
import com.investigawarma.app.ui.components.StarRow
import com.investigawarma.app.ui.components.ZoneIcon
import com.investigawarma.app.ui.viewmodel.MissionWithProgress
import com.investigawarma.app.ui.viewmodel.ViewModelFactory
import com.investigawarma.app.ui.viewmodel.ZoneViewModel

/**
 * Pantalla de zona: muestra las misiones como un camino de progreso (no una
 * lista plana de botones), con estado visual claro (bloqueada/disponible/
 * iniciada/completada/dominada) y los desafíos cortos de la zona.
 */
@Composable
fun ZoneScreen(
    factory: ViewModelFactory,
    zone: Zone,
    accentColor: Color,
    onBack: () -> Unit,
    onMissionClick: (String) -> Unit,
    onStatsClick: () -> Unit = {},
) {
    val viewModel: ZoneViewModel = viewModel(factory = factory)
    LaunchedEffect(zone) { viewModel.loadZone(zone) }
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás") }
            ZoneIcon(zone = zone, color = accentColor, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(8.dp))
            Text(zoneTitle(zone), style = MaterialTheme.typography.titleLarge)
        }

        if (state.missions.isEmpty() && !state.isLoading) {
            EmptyState(title = "Sin misiones todavía", message = "Esta zona no tiene misiones cargadas.")
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (zone == Zone.CENTRO_DE_DATOS) {
                item {
                    AppCard(onClick = onStatsClick) {
                        Text("Ver mis estadísticas", style = MaterialTheme.typography.titleMedium, color = accentColor)
                        Text("Gráficos y números reales de tu progreso", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { SectionHeader(title = "Casos por resolver", subtitle = "Sigue el camino y resuelve cada caso") }
            items(state.missions) { mwp ->
                MissionRow(mwp, accentColor, onClick = { if (canPlay(mwp)) onMissionClick(mwp.mission.id) })
            }
            if (state.challenges.isNotEmpty()) {
                item { SectionHeader(title = "Desafíos rápidos", subtitle = "Practica lo aprendido en esta zona") }
                items(state.challenges) { challenge ->
                    AppCard {
                        Text(challenge.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Tipo: ${challengeTypeLabel(challenge.type)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }
    }
}

private fun canPlay(mwp: MissionWithProgress): Boolean =
    mwp.progress?.status != "LOCKED"

private fun zoneTitle(zone: Zone) = when (zone) {
    Zone.SALA_OBSERVACION -> "Jardín de la Lupa"
    Zone.LABORATORIO_EXPERIMENTAL -> "Taller de Inventos"
    Zone.BIODESCUBRIMIENTO -> "Bosque y Costa Salvaje"
    Zone.PLANETA_TIERRA -> "Expedición al Planeta"
    Zone.CENTRO_DE_DATOS -> "Observatorio de Pistas"
    Zone.MUSEO_CIENTIFICO -> "Museo Científico"
}

private fun challengeTypeLabel(type: String) = when (type) {
    "DETECTIVE" -> "Detective científico"
    "ORDER" -> "Ordena la investigación"
    "PATTERN" -> "Encuentra el patrón"
    "CLASSIFY" -> "Clasifica descubrimientos"
    "BUILD" -> "Construye el experimento"
    else -> type
}

@Composable
private fun MissionRow(mwp: MissionWithProgress, accentColor: Color, onClick: () -> Unit) {
    val status = mwp.progress?.status ?: "LOCKED"
    val locked = status == "LOCKED"
    AppCard(onClick = if (!locked) onClick else null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = if (locked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) else accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    when {
                        locked -> Icon(Icons.Filled.Lock, contentDescription = "Bloqueada", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        status == "COMPLETED" || status == "MASTERED" -> Icon(Icons.Filled.CheckCircle, contentDescription = "Completada", tint = accentColor)
                        else -> Icon(Icons.Filled.PlayArrow, contentDescription = "Disponible", tint = accentColor)
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(mwp.mission.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    statusLabel(status, mwp.mission.difficulty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
            StarRow(count = if (status == "MASTERED") mwp.mission.starReward else 0, total = mwp.mission.starReward)
        }
    }
}

private fun statusLabel(status: String, difficulty: Int): String {
    val diffLabel = "Dificultad ${"★".repeat(difficulty)}"
    return when (status) {
        "LOCKED" -> "Bloqueado · $diffLabel"
        "AVAILABLE" -> "Caso nuevo · $diffLabel"
        "STARTED" -> "En investigación · $diffLabel"
        "COMPLETED" -> "Caso resuelto · $diffLabel"
        "MASTERED" -> "¡Caso resuelto a la perfección! · $diffLabel"
        else -> diffLabel
    }
}
