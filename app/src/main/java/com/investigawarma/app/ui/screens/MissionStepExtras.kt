package com.investigawarma.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.investigawarma.app.data.local.entity.ScientificMissionEntity
import com.investigawarma.app.domain.model.Zone
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.ExperimentLiveVisual
import com.investigawarma.app.ui.components.IllustrationCatalog
import com.investigawarma.app.ui.components.IrisExpression
import com.investigawarma.app.ui.components.PrimaryButton
import com.investigawarma.app.ui.components.SceneIllustration
import com.investigawarma.app.ui.components.SecondaryButton
import com.investigawarma.app.ui.components.StarRow
import com.investigawarma.app.ui.viewmodel.MissionUiState
import com.investigawarma.app.ui.viewmodel.MissionViewModel

@Composable
fun HypothesisStep(viewModel: MissionViewModel, state: MissionUiState) {
    var customMode by remember { mutableStateOf(false) }
    var variable by remember { mutableStateOf(state.hypothesisVariable) }
    var result by remember { mutableStateOf(state.hypothesisResult) }
    var explanation by remember { mutableStateOf(state.hypothesisExplanation) }
    val topic = state.mission?.tags?.firstOrNull() ?: "esto"
    val guesses = listOf("Va a cambiar mucho", "Casi no va a cambiar")

    AppCard {
        Text("🔮 ¿Qué crees que va a pasar?", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        if (!customMode) {
            guesses.forEach { guess ->
                OptionCard(text = guess, selected = state.hypothesisValid && state.hypothesisResult == guess) {
                    viewModel.submitHypothesis(
                        topic,
                        guess,
                        "Lo pienso por lo que observé en $topic.",
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(4.dp))
            SecondaryButton(text = "Explícalo con tus palabras", onClick = { customMode = true }, modifier = Modifier.fillMaxWidth())
        } else {
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
        }
        state.hypothesisFeedback?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                if (state.hypothesisValid) "¡Buena hipótesis! Vamos a comprobarlo." else it,
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
        "find_spot" -> FindSpotContent(viewModel, state)
        else -> AppCard { Text(content.instructions ?: "Continúa con la misión.") }
    }
}

/** "Busca y Descubre": tocar la escena ilustrada para encontrar detalles escondidos. */
@Composable
private fun FindSpotContent(viewModel: MissionViewModel, state: MissionUiState) {
    val mission = state.mission ?: return
    val content = state.experimentContent ?: return
    val spots = content.spots ?: emptyList()
    var found by remember(mission.id) { mutableStateOf(setOf<Int>()) }
    val allFound = spots.isNotEmpty() && found.size >= spots.size

    AppCard {
        Text("🔎 Busca y descubre", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            if (state.experimentCompleted) "" else if (allFound) "¡Los encontraste todos!" else "Toca la escena para encontrar ${spots.size - found.size} detalle(s) más.",
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        ) {
            SceneIllustration(
                key = IllustrationCatalog.forMission(mission.id),
                tint = missionTint(mission),
                modifier = Modifier.fillMaxSize(),
            )
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(spots, state.experimentCompleted) {
                        detectTapGestures { offset ->
                            if (state.experimentCompleted) return@detectTapGestures
                            val relX = offset.x / size.width
                            val relY = offset.y / size.height
                            spots.forEachIndexed { i, spot ->
                                if (i !in found) {
                                    val dx = relX - spot.x
                                    val dy = relY - spot.y
                                    if (dx * dx + dy * dy <= spot.radius * spot.radius) {
                                        found = found + i
                                    }
                                }
                            }
                        }
                    },
            ) {
                spots.forEachIndexed { i, spot ->
                    if (i in found) {
                        val center = Offset(spot.x * size.width, spot.y * size.height)
                        val r = size.minDimension * spot.radius
                        drawCircle(Color(0x552ECC71), radius = r, center = center)
                        drawCircle(Color(0xFF2ECC71), radius = r, center = center, style = Stroke(width = r * 0.2f))
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        if (state.experimentCompleted) {
            Text(state.experimentResultSummary ?: "", style = MaterialTheme.typography.bodyLarge)
        } else if (allFound) {
            PrimaryButton(
                text = "Continuar",
                onClick = { viewModel.submitFindSpotAnswer(found.size, spots.size) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SimulatorContent(viewModel: MissionViewModel, state: MissionUiState) {
    AppCard {
        Text("¡Mueve los controles!", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ExperimentLiveVisual(
            experimentId = state.experimentContent?.experimentId,
            parameters = state.experimentParameters,
            values = state.experimentValues,
            modifier = Modifier.fillMaxWidth().height(150.dp),
        )
        Spacer(Modifier.height(12.dp))
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
        Text("👀 " + (content.prompt ?: "Marca las diferencias reales."), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        statements.forEachIndexed { i, stmt ->
            ToggleCard(text = stmt, checked = i in selected, enabled = !state.experimentCompleted) { checked ->
                selected = if (checked) selected + i else selected - i
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(4.dp))
        if (!state.experimentCompleted) {
            PrimaryButton(text = "Confirmar observación", onClick = { viewModel.submitCompareAnswer(selected) }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(state.experimentResultSummary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** Tarjeta grande de selección múltiple, con marca visible en vez de un Checkbox diminuto. */
@Composable
private fun ToggleCard(text: String, checked: Boolean, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (checked) Color(0xFF2ECC71).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onToggle(!checked) },
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(50),
                color = if (checked) Color(0xFF2ECC71) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                modifier = Modifier.size(26.dp),
            ) {
                if (checked) {
                    Text("✓", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.White)
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
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
        Text("🔗 " + (content.prompt ?: "Empareja cada palabra con su definición."), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Toca una palabra y luego su definición.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            pairs.forEachIndexed { i, pair ->
                val isMatched = matches.containsKey(pair.label)
                val isSelected = selectedLabel == pair.label
                val color = TilePalette[i % TilePalette.size]
                ChoiceChip(
                    text = pair.label,
                    selected = isSelected || isMatched,
                    color = if (isMatched) Color(0xFF2ECC71) else color,
                ) { if (!isMatched && !state.experimentCompleted) selectedLabel = pair.label }
                Spacer(Modifier.width(8.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Column {
            shuffledDefinitions.forEach { def ->
                val usedFor = matches.entries.firstOrNull { it.value == def }?.key
                ToggleCard(text = if (usedFor != null) "$usedFor → $def" else def, checked = usedFor != null, enabled = !state.experimentCompleted) {
                    val label = selectedLabel
                    if (label != null && usedFor == null && !state.experimentCompleted) {
                        matches = matches + (label to def)
                        selectedLabel = null
                    }
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
        Text("🔮 ¿Qué crees que pasará?", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        options.forEachIndexed { i, opt ->
            val color = TilePalette[i % TilePalette.size]
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selected == i) color.copy(alpha = 0.28f) else color.copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !state.experimentCompleted) { selected = i },
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    LetterBadge((i + 1).toString(), color)
                    Spacer(Modifier.width(12.dp))
                    Text(opt, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(4.dp))
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
            label = { Text("Tu conclusión (opcional)") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun DiscoveryPreview(mission: ScientificMissionEntity, promptText: String) {
    val tint = missionTint(mission)
    AppCard {
        SceneIllustration(
            key = IllustrationCatalog.forMission(mission.id),
            tint = tint,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(promptText, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DiscoveryCelebration(mission: ScientificMissionEntity, title: String, xp: Int, stars: Int, onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text("¡Lo descubriste! 🎉", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        SceneIllustration(
            key = IllustrationCatalog.forMission(mission.id),
            tint = missionTint(mission),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            "🏛️ Se guardó en tu Museo",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Spacer(Modifier.height(16.dp))
        StarRow(count = stars, total = 3)
        Spacer(Modifier.height(8.dp))
        Text("+$xp XP", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
        Spacer(Modifier.height(32.dp))
        PrimaryButton(text = "Volver a la Academia", onClick = onContinue, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun missionTint(mission: ScientificMissionEntity): Color =
    runCatching { Zone.valueOf(mission.zone) }.getOrNull()?.let { zoneTint(it) }
        ?: MaterialTheme.colorScheme.primary
