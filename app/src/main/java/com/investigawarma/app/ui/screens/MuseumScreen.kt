package com.investigawarma.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.SectionHeader
import com.investigawarma.app.ui.theme.SparkAmber
import com.investigawarma.app.ui.viewmodel.MuseumViewModel
import com.investigawarma.app.ui.viewmodel.ViewModelFactory

/**
 * Museo Científico Personal: coleccionables e insignias, desbloqueados
 * realmente por progreso (no decorativos). Cada objeto muestra su condición
 * de desbloqueo cuando aún no se ha conseguido.
 */
@Composable
fun MuseumScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: MuseumViewModel = viewModel(factory = factory)
    val items by viewModel.collectionItems.collectAsState()
    val badges by viewModel.badges.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás") }
            Text("Museo Científico", style = MaterialTheme.typography.titleLarge)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionHeader(title = "Colección", subtitle = "${items.count { it.unlockedAt != null }} de ${items.size} objetos")
            }
            items(items) { item ->
                val unlocked = item.unlockedAt != null
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (unlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                            contentDescription = if (unlocked) "Desbloqueado" else "Bloqueado",
                            tint = if (unlocked) SparkAmber else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(item.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                if (unlocked) item.description else item.requirementDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            )
                        }
                    }
                }
            }

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionHeader(title = "Insignias", subtitle = "${badges.count { it.unlockedAt != null }} de ${badges.size} conseguidas")
            }
            items(badges) { badge ->
                val unlocked = badge.unlockedAt != null
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (unlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                            contentDescription = if (unlocked) "Insignia desbloqueada" else "Insignia bloqueada",
                            tint = if (unlocked) SparkAmber else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(badge.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                badge.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            )
                        }
                    }
                }
            }
        }
    }
}
