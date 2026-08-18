package com.investigawarma.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.EmptyState
import com.investigawarma.app.ui.components.PrimaryButton
import com.investigawarma.app.ui.components.SectionHeader
import com.investigawarma.app.ui.viewmodel.JournalViewModel
import com.investigawarma.app.ui.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Diario científico: notas de texto y "diario hablado" (grabación real con
 * MediaRecorder, máx. 60s). Si el permiso de micrófono se deniega, se explica
 * brevemente y se ofrece la alternativa de texto; el resto de la app sigue funcional.
 */
@Composable
fun JournalScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: JournalViewModel = viewModel(factory = factory)
    val entries by viewModel.entries.collectAsState()
    val recordingState by viewModel.recordingState.collectAsState()
    val playingEntryId by viewModel.playingEntryId.collectAsState()
    val context = LocalContext.current

    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) viewModel.startRecording() else viewModel.onPermissionDenied()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás") }
            Text("Diario Científico", style = MaterialTheme.typography.titleLarge)
        }

        LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                AppCard {
                    Text("Nueva nota", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = noteTitle, onValueChange = { noteTitle = it.take(60) }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = noteContent, onValueChange = { noteContent = it.take(2000) }, label = { Text("¿Qué descubriste?") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton(
                        text = "Guardar nota",
                        enabled = noteContent.isNotBlank(),
                        onClick = {
                            viewModel.addTextEntry(noteTitle, noteContent)
                            noteTitle = ""; noteContent = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item {
                AppCard {
                    Text("Diario hablado", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("Graba una observación de hasta 60 segundos.", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    if (recordingState.permissionDenied) {
                        Text(
                            "No se concedió el permiso de micrófono. Puedes seguir usando notas de texto sin problema.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    if (recordingState.isRecording) {
                        Text("Grabando… ${recordingState.elapsedSeconds}s / 60s", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton(
                            text = "Detener y guardar",
                            onClick = { viewModel.stopRecordingAndSave(noteTitle.ifBlank { "Nota de voz" }) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        PrimaryButton(
                            text = "Grabar observación",
                            onClick = {
                                val granted = androidx.core.content.ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.RECORD_AUDIO,
                                ) == PackageManager.PERMISSION_GRANTED
                                if (granted) viewModel.startRecording() else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item { SectionHeader(title = "Tus entradas", subtitle = "${entries.size} registradas") }

            if (entries.isEmpty()) {
                item { EmptyState(title = "Tu diario está vacío", message = "Completa misiones o graba una nota para empezar tu cuaderno.") }
            }

            items(entries) { entry ->
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (entry.type == "VOICE") {
                            IconButton(onClick = { viewModel.togglePlayback(entry.id) }) {
                                Icon(
                                    if (playingEntryId == entry.id) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    contentDescription = if (playingEntryId == entry.id) "Detener" else "Reproducir nota de voz",
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                SimpleDateFormat("d MMM yyyy, HH:mm", Locale("es")).format(Date(entry.createdAt)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            )
                            if (entry.content.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(entry.content, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
