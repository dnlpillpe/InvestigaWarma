package com.investigawarma.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.IrisExpression
import com.investigawarma.app.ui.components.PrimaryButton
import com.investigawarma.app.ui.components.StarRow
import com.investigawarma.app.ui.viewmodel.MissionUiState
import com.investigawarma.app.ui.viewmodel.MissionViewModel

@Composable
fun HypothesisStep(viewModel: MissionViewModel, state: MissionUiState) {
    var variable by remember { mutableStateOf(state.hypothesisVariable) }
    var result by remember { mutableStateOf(state.hypothesisResult) }
    var explanation by remember { mutableStateOf(state.hypothesisExplanation) }

    AppCard {
        Text("Construye tu hipótesis", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("SI...", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(value = variable, onValueChange = { variable = it }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text("ENTONCES...", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(value = result, onValueChange = { result = it }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text("PORQUE...", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(value = explanation, onValueChange = { explanation = it }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            text = "Comprobar hipótesis",
            onClick = { viewModel.submitHypothesis(variable, result, explanation) },
            modifier = Modifier.fillMaxWidth(),
        )
        state.hypothesisFeedback?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                if (state.hypothesisValid) "¡Buena hipótesis! Un buen investigador la pone a prueba." else it,
                color = if (state.hypothesisValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun ExperimentStep(viewModel: MissionViewModel, state: MissionUiState) {
    val content = state.experimentContent ?: return
    when (content.kind) {
        "experiment" -> SimulatorContent(viewModel, state)
        "challenge" -> ChallengeContent(viewModel, state)
        "compare" -> CompareContent(viewModel, state)
        "drag_drop" -> DragDropContent(viewModel, state)
        "predict" -> PredictContent(viewModel, state)
        else -> AppCard { Text(content.instructions ?: "Continúa con la misión.") }
    }
}

@Composable
private fun SimulatorContent(viewModel: MissionViewModel, state: MissionUiState) {
    AppCard {
        Text("Ajusta las variables del experimento", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        state.experimentParameters.forEach { param ->
            val value = state.experimentValues[param.name] ?: param.defaultValue
            Text("${param.name} (${param.unit}): ${"%.1f".format(value)}", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = value,
                onValueChange = { viewModel.updateExperimentValue(param.name, it) },
                valueRange = param.minValue..param.maxValue,
                enabled = !state.experimentCompleted,
            )
        }
        Spacer(Modifier.height(8.dp))
        if (!state.experimentCompleted) {
            PrimaryButton(text = "Ejecutar experimento", onClick = { viewModel.runSimulatorExperiment() }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(state.experimentResultSummary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun CompareContent(viewModel: MissionViewModel, state: MissionUiState) {
    val content = state.experimentContent ?: return
    val statements = content.statements ?: emptyList()
    var selected by remember { mutableStateOf(setOf<Int>()) }

    AppCard {
        Text(content.prompt ?: "Marca las diferencias reales.", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        statements.forEachIndexed { i, stmt ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = i in selected,
                    enabled = !state.experimentCompleted,
                    onCheckedChange = { checked ->
                        selected = if (checked) selected + i else selected - i
                    },
                )
                Text(stmt, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(8.dp))
        if (!state.experimentCompleted) {
            PrimaryButton(text = "Confirmar observación", onClick = { viewModel.submitCompareAnswer(selected) }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(state.experimentResultSummary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * Emparejar (arrastrar simplificado): el jugador toca una etiqueta y luego la
 * definición correspondiente para formar la pareja. Interacción real y
 * evaluable, alternativa táctil al arrastre físico en pantallas pequeñas.
 */
@Composable
private fun DragDropContent(viewModel: MissionViewModel, state: MissionUiState) {
    val content = state.experimentContent ?: return
    val pairs = content.pairs ?: emptyList()
    var selectedLabel by remember { mutableStateOf<String?>(null) }
    var matches by remember { mutableStateOf(mapOf<String, String>()) }
    val shuffledDefinitions = remember(pairs) { pairs.map { it.definition }.shuffled() }

    AppCard {
        Text(content.prompt ?: "Empareja cada palabra con su definición.", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("1. Toca una palabra. 2. Toca su definición.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            pairs.forEach { pair ->
                val isMatched = matches.containsKey(pair.label)
                androidx.compose.material3.AssistChip(
                    onClick = { if (!isMatched && !state.experimentCompleted) selectedLabel = pair.label },
                    label = { Text(pair.label) },
                    enabled = !isMatched && !state.experimentCompleted,
                    modifier = Modifier.padding(end = 6.dp, bottom = 6.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Column {
            shuffledDefinitions.forEach { def ->
                val usedFor = matches.entries.firstOrNull { it.value == def }?.key
                AppCard(
                    onClick = {
                        val label = selectedLabel
                        if (label != null && usedFor == null && !state.experimentCompleted) {
                            matches = matches + (label to def)
                            selectedLabel = null
                        }
                    },
                ) {
                    Text(
                        if (usedFor != null) "$usedFor → $def" else def,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(Modifier.height(6.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        if (!state.experimentCompleted) {
            PrimaryButton(
                text = "Confirmar emparejamiento",
                enabled = matches.size == pairs.size,
                onClick = { viewModel.submitDragDropAnswer(matches) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(state.experimentResultSummary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun PredictContent(viewModel: MissionViewModel, state: MissionUiState) {
    val content = state.experimentContent ?: return
    val options = content.options ?: emptyList()
    var selected by remember { mutableStateOf<Int?>(null) }

    AppCard {
        Text("¿Qué crees que pasará?", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        options.forEachIndexed { i, opt ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selected == i,
                    enabled = !state.experimentCompleted,
                    onClick = { selected = i },
                )
                Text(opt, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(8.dp))
        if (!state.experimentCompleted) {
            PrimaryButton(
                text = "Predecir",
                enabled = selected != null,
                onClick = { selected?.let { viewModel.submitPredictAnswer(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(state.experimentResultSummary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun AnalyzeStep(promptText: String, viewModel: MissionViewModel, state: MissionUiState) {
    var note by remember { mutableStateOf(state.analyzeNote) }
    AppCard {
        Text(promptText, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        state.experimentResultSummary?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = note,
            onValueChange = { note = it; viewModel.updateAnalyzeNote(it) },
            label = { Text("Escribe una conclusión (opcional, se guarda en tu diario)") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun DiscoveryPreview(promptText: String) {
    AppCard { Text(promptText, style = MaterialTheme.typography.bodyLarge) }
}

@Composable
fun DiscoveryCelebration(title: String, xp: Int, stars: Int, onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Text("¡Descubrimiento desbloqueado!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        StarRow(count = stars, total = 3)
        Spacer(Modifier.height(8.dp))
        Text("+$xp XP", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
        Spacer(Modifier.height(32.dp))
        PrimaryButton(text = "Volver a la Academia", onClick = onContinue, modifier = Modifier.fillMaxWidth())
    }
}
