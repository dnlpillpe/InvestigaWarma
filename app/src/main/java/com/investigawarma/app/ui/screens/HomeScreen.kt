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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.domain.model.Zone
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.AvatarIcon
import com.investigawarma.app.ui.components.IrisExpression
import com.investigawarma.app.ui.components.IrisMessageBubble
import com.investigawarma.app.ui.components.ZoneIcon
import com.investigawarma.app.ui.components.XpBar
import com.investigawarma.app.ui.theme.ZoneBio
import com.investigawarma.app.ui.theme.ZoneDatos
import com.investigawarma.app.ui.theme.ZoneLaboratorio
import com.investigawarma.app.ui.theme.ZoneMuseo
import com.investigawarma.app.ui.theme.ZoneObservacion
import com.investigawarma.app.ui.theme.ZonePlaneta
import com.investigawarma.app.ui.viewmodel.HomeViewModel
import com.investigawarma.app.ui.viewmodel.ViewModelFactory

private fun zoneColor(zone: Zone) = when (zone) {
    Zone.SALA_OBSERVACION -> ZoneObservacion
    Zone.LABORATORIO_EXPERIMENTAL -> ZoneLaboratorio
    Zone.BIODESCUBRIMIENTO -> ZoneBio
    Zone.PLANETA_TIERRA -> ZonePlaneta
    Zone.CENTRO_DE_DATOS -> ZoneDatos
    Zone.MUSEO_CIENTIFICO -> ZoneMuseo
}

private fun zoneLabel(zone: Zone) = when (zone) {
    Zone.SALA_OBSERVACION -> "Jardín de la Lupa"
    Zone.LABORATORIO_EXPERIMENTAL -> "Taller de Inventos"
    Zone.BIODESCUBRIMIENTO -> "Bosque y Costa Salvaje"
    Zone.PLANETA_TIERRA -> "Expedición al Planeta"
    Zone.CENTRO_DE_DATOS -> "Observatorio de Pistas"
    Zone.MUSEO_CIENTIFICO -> "Archivo de Casos"
}

private fun zoneTagline(zone: Zone) = when (zone) {
    Zone.SALA_OBSERVACION -> "Tus primeros pasos como explorador"
    Zone.LABORATORIO_EXPERIMENTAL -> "Arma y prueba tus propios inventos"
    Zone.BIODESCUBRIMIENTO -> "Plantas, animales y criaturas por descubrir"
    Zone.PLANETA_TIERRA -> "Clima, agua y tierra en movimiento"
    Zone.CENTRO_DE_DATOS -> "Descifra pistas escondidas en los números"
    Zone.MUSEO_CIENTIFICO -> ""
}

/**
 * Mapa principal de la Academia Científica: centro de experiencia del jugador.
 * Muestra avatar, nivel, progreso y las seis zonas explorables. No es un menú plano.
 */
@Composable
fun HomeScreen(
    factory: ViewModelFactory,
    onZoneClick: (Zone) -> Unit,
    onJournalClick: () -> Unit,
    onMuseumClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarIcon(avatarId = state.profile?.avatarId ?: 0, modifier = Modifier.size(48.dp), selected = true)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    state.profile?.alias ?: "Detective",
                    style = MaterialTheme.typography.titleLarge,
                )
                XpBar(levelName = state.levelName, progressPercent = state.levelProgressPercent)
            }
            IconButton(onClick = onJournalClick) {
                Icon(Icons.Filled.AutoStories, contentDescription = "Cuaderno de pistas")
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Filled.Settings, contentDescription = "Ajustes")
            }
        }

        IrisMessageBubble(
            message = "¿Qué caso resolvemos hoy? 🕵️",
            expression = IrisExpression.FELIZ,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.zones) { zoneSummary ->
                ZoneMapCard(
                    zone = zoneSummary.zone,
                    completed = zoneSummary.completed,
                    total = zoneSummary.total,
                    unlocked = zoneSummary.isUnlocked,
                    onClick = {
                        if (zoneSummary.zone == Zone.MUSEO_CIENTIFICO) onMuseumClick() else onZoneClick(zoneSummary.zone)
                    },
                )
            }
        }
    }
}

@Composable
private fun ZoneMapCard(
    zone: Zone,
    completed: Int,
    total: Int,
    unlocked: Boolean,
    onClick: () -> Unit,
) {
    val color = zoneColor(zone)
    AppCard(onClick = if (unlocked || zone == Zone.MUSEO_CIENTIFICO) onClick else null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    ZoneIcon(zone = zone, color = color, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(zoneLabel(zone), style = MaterialTheme.typography.titleMedium)
                if (zone != Zone.MUSEO_CIENTIFICO) {
                    Text(
                        if (unlocked) zoneTagline(zone) else "Bloqueada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                    if (unlocked) {
                        Text(
                            "$completed de $total casos resueltos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    }
                } else {
                    Text(
                        "Tus casos resueltos e insignias",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
            }
            if (!unlocked && zone != Zone.MUSEO_CIENTIFICO) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Bloqueado",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                )
            }
        }
    }
}
