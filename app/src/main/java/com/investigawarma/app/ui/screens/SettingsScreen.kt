package com.investigawarma.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.ConfirmationDialog
import com.investigawarma.app.ui.components.SectionHeader
import com.investigawarma.app.ui.viewmodel.SettingsViewModel
import com.investigawarma.app.ui.viewmodel.ViewModelFactory

@Composable
fun SettingsScreen(factory: ViewModelFactory, onBack: () -> Unit, onProgressReset: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel(factory = factory)
    val profile by viewModel.profile.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás") }
            Text("Ajustes", style = MaterialTheme.typography.titleLarge)
        }

        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            item {
                SectionHeader(title = "Sonido y vibración")
                AppCard {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Efectos de sonido", modifier = Modifier.weight(1f))
                        Switch(
                            checked = profile?.soundEnabled ?: true,
                            onCheckedChange = { viewModel.setSoundEnabled(it) },
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Vibración (háptica)", modifier = Modifier.weight(1f))
                        Switch(
                            checked = profile?.hapticsEnabled ?: true,
                            onCheckedChange = { viewModel.setHapticsEnabled(it) },
                        )
                    }
                }
            }
            item {
                SectionHeader(title = "Privacidad")
                AppCard {
                    Text(
                        "InvestigaWarma no usa internet ni recopila datos personales. Tu alias, avatar y progreso se guardan únicamente en este dispositivo.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                SectionHeader(title = "Datos")
                AppCard(onClick = { showResetDialog = true }) {
                    Text("Reiniciar todo el progreso", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                    Text("Borra tu perfil, misiones y diario. No se puede deshacer.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (showResetDialog) {
        ConfirmationDialog(
            title = "¿Reiniciar progreso?",
            message = "Se borrará tu perfil y tendrás que empezar de nuevo. Esta acción no se puede deshacer.",
            confirmLabel = "Reiniciar",
            onConfirm = {
                showResetDialog = false
                viewModel.resetProgress(onProgressReset)
            },
            onDismiss = { showResetDialog = false },
        )
    }
}
