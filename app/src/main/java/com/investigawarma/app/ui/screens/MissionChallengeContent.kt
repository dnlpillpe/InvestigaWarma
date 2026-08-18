package com.investigawarma.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.investigawarma.app.domain.model.BuildPayload
import com.investigawarma.app.domain.model.ClassifyPayload
import com.investigawarma.app.domain.model.DetectivePayload
import com.investigawarma.app.domain.model.OrderPayload
import com.investigawarma.app.domain.model.PatternPayload
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.PrimaryButton
import com.investigawarma.app.ui.viewmodel.MissionUiState
import com.investigawarma.app.ui.viewmodel.MissionViewModel
import kotlinx.serialization.json.Json

private val challengeJson = Json { ignoreUnknownKeys = true }

/** Renderiza el minijuego correspondiente al tipo del desafío ligado a esta misión. */
@Composable
fun ChallengeContent(viewModel: MissionViewModel, state: MissionUiState) {
    val challenge = state.challenge ?: return
    when (challenge.type) {
        "DETECTIVE" -> DetectiveChallenge(challenge.dataJson, state.experimentCompleted, state.experimentResultSummary) {
            viewModel.submitDetectiveAnswer(it)
        }
        "ORDER" -> OrderChallenge(challenge.dataJson, state.experimentCompleted, state.experimentResultSummary) {
            viewModel.submitOrderAnswer(it)
        }
        "PATTERN" -> PatternChallenge(challenge.dataJson, state.experimentCompleted, state.experimentResultSummary) {
            viewModel.submitPatternAnswer(it)
        }
        "CLASSIFY" -> ClassifyChallenge(challenge.dataJson, state.experimentCompleted, state.experimentResultSummary) {
            viewModel.submitClassifyAnswer(it)
        }
        "BUILD" -> BuildChallenge(challenge.dataJson, state.experimentCompleted, state.experimentResultSummary) {
            viewModel.submitBuildAnswer(it)
        }
    }
}

@Composable
private fun DetectiveChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (Int) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(DetectivePayload.serializer(), dataJson) }
    var selected by remember { mutableStateOf<Int?>(null) }
    AppCard {
        Text("Encuentra la afirmación incorrecta", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        payload.statements.forEachIndexed { i, s ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selected == i, enabled = !completed, onClick = { selected = i })
                Text(s, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(8.dp))
        if (!completed) {
            PrimaryButton(text = "Acusar", enabled = selected != null, onClick = { selected?.let(onSubmit) }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(summary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun OrderChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (List<String>) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(OrderPayload.serializer(), dataJson) }
    var order by remember { mutableStateOf(payload.steps.shuffled()) }
    AppCard {
        Text("Ordena la investigación", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("Toca dos elementos para intercambiarlos.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        var swapIndex by remember { mutableStateOf<Int?>(null) }
        order.forEachIndexed { i, item ->
            AppCard(
                onClick = {
                    if (completed) return@AppCard
                    val pending = swapIndex
                    if (pending == null) {
                        swapIndex = i
                    } else {
                        order = order.toMutableList().apply {
                            val tmp = this[pending]
                            this[pending] = this[i]
                            this[i] = tmp
                        }
                        swapIndex = null
                    }
                },
            ) {
                Text("${i + 1}. $item", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(8.dp))
        if (!completed) {
            PrimaryButton(text = "Confirmar orden", onClick = { onSubmit(order) }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(summary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun PatternChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (Int) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(PatternPayload.serializer(), dataJson) }
    var answer by remember { mutableStateOf("") }
    AppCard {
        Text("¿Cuál es el siguiente número?", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(payload.sequence.joinToString(", ") + ", ?", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it.filter { c -> c.isDigit() || c == '-' } },
            enabled = !completed,
            label = { Text("Tu respuesta") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        if (!completed) {
            PrimaryButton(
                text = "Confirmar patrón",
                enabled = answer.toIntOrNull() != null,
                onClick = { answer.toIntOrNull()?.let(onSubmit) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(summary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ClassifyChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (Map<String, String>) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(ClassifyPayload.serializer(), dataJson) }
    var answers by remember { mutableStateOf(mapOf<String, String>()) }
    AppCard {
        Text("Clasifica cada elemento", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        payload.items.forEach { item ->
            Text(item.label, style = MaterialTheme.typography.bodyMedium)
            Row {
                payload.categories.forEach { cat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = answers[item.label] == cat,
                            enabled = !completed,
                            onClick = { answers = answers + (item.label to cat) },
                        )
                        Text(cat, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
        }
        if (!completed) {
            PrimaryButton(
                text = "Confirmar clasificación",
                enabled = answers.size == payload.items.size,
                onClick = { onSubmit(answers) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(summary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun BuildChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (Set<String>) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(BuildPayload.serializer(), dataJson) }
    val allInstruments = remember(dataJson) { (payload.correctInstruments + payload.distractorInstruments).shuffled() }
    var selected by remember { mutableStateOf(setOf<String>()) }
    AppCard {
        Text("Elige los instrumentos correctos", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        allInstruments.forEach { instrument ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = instrument in selected,
                    enabled = !completed,
                    onCheckedChange = { checked -> selected = if (checked) selected + instrument else selected - instrument },
                )
                Text(instrument, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(8.dp))
        if (!completed) {
            PrimaryButton(text = "Armar experimento", enabled = selected.isNotEmpty(), onClick = { onSubmit(selected) }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(summary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
