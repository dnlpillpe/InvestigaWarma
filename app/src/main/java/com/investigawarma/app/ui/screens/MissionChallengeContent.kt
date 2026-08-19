package com.investigawarma.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

val TilePalette = listOf(
    Color(0xFF8B75F0), Color(0xFF20D3C2), Color(0xFFFFB020), Color(0xFFFF6B6B), Color(0xFF2ECC71),
)

/** Renderiza el minijuego correspondiente al tipo del desafío ligado a esta misión.
 * Cada tipo tiene su propia forma de tarjeta e interacción (nada de listas idénticas). */
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

// ---------------------------------------------------------------------------
// DETECTIVE — tarjetas de evidencia completas y tocables, con lupa.
// ---------------------------------------------------------------------------

@Composable
private fun DetectiveChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (Int) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(DetectivePayload.serializer(), dataJson) }
    var selected by remember { mutableStateOf<Int?>(null) }
    AppCard {
        Text("🔍 Encuentra lo falso", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Toca la tarjeta que NO es verdad.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(10.dp))
        payload.statements.forEachIndexed { i, s ->
            EvidenceCard(text = s, letter = ('A' + i).toString(), selected = selected == i, enabled = !completed) { selected = i }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(4.dp))
        if (!completed) {
            PrimaryButton(text = "¡Lo encontré!", enabled = selected != null, onClick = { selected?.let(onSubmit) }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(summary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun EvidenceCard(text: String, letter: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFFFFB020).copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            LetterBadge(letter, if (selected) Color(0xFFFFB020) else MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun LetterBadge(letter: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color, modifier = Modifier.size(32.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(4.dp))
            Text(letter, style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
    }
}

// ---------------------------------------------------------------------------
// ORDER — tarjetas con insignia numerada de color, se tocan dos para cambiar.
// ---------------------------------------------------------------------------

@Composable
private fun OrderChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (List<String>) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(OrderPayload.serializer(), dataJson) }
    var order by remember { mutableStateOf(payload.steps.shuffled()) }
    var swapIndex by remember { mutableStateOf<Int?>(null) }
    AppCard {
        Text("🔢 Ordena la investigación", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Toca dos tarjetas para cambiarlas de lugar.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(10.dp))
        order.forEachIndexed { i, item ->
            val color = TilePalette[i % TilePalette.size]
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (swapIndex == i) color.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !completed) {
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
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    LetterBadge((i + 1).toString(), color)
                    Spacer(Modifier.width(12.dp))
                    Text(item, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        if (!completed) {
            PrimaryButton(text = "Confirmar orden", onClick = { onSubmit(order) }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(summary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// ---------------------------------------------------------------------------
// PATTERN — fichas numéricas grandes + teclado de chips (sin teclado del SO).
// ---------------------------------------------------------------------------

@Composable
private fun PatternChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (Int) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(PatternPayload.serializer(), dataJson) }
    var answer by remember { mutableStateOf("") }
    AppCard {
        Text("🧩 ¿Qué número sigue?", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Mira la serie y adivina el número que falta.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            payload.sequence.forEach { n -> NumberTile(text = n.toString(), color = Color(0xFF20D3C2)) }
            NumberTile(text = answer.ifEmpty { "?" }, color = Color(0xFFFFB020))
        }
        Spacer(Modifier.height(16.dp))
        if (!completed) {
            NumberKeypad(
                enabled = true,
                onKey = { key ->
                    when (key) {
                        "⌫" -> answer = answer.dropLast(1)
                        "-" -> answer = if (answer.startsWith("-")) answer.drop(1) else "-$answer"
                        else -> answer += key
                    }
                },
            )
            Spacer(Modifier.height(12.dp))
            PrimaryButton(
                text = "Confirmar",
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
private fun NumberTile(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(14.dp), color = color.copy(alpha = 0.22f), modifier = Modifier.size(52.dp)) {
        Column(modifier = Modifier.fillMaxWidth().height(52.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text, style = MaterialTheme.typography.titleLarge, color = color)
        }
    }
}

@Composable
private fun NumberKeypad(enabled: Boolean, onKey: (String) -> Unit) {
    val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("-", "0", "⌫"))
    Column {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(enabled = enabled) { onKey(key) },
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(key, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// CLASSIFY — un elemento a la vez, categorías como canastas de color.
// ---------------------------------------------------------------------------

@Composable
private fun ClassifyChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (Map<String, String>) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(ClassifyPayload.serializer(), dataJson) }
    var answers by remember { mutableStateOf(mapOf<String, String>()) }
    var index by remember { mutableStateOf(0) }
    val item = payload.items.getOrNull(index)
    AppCard {
        Text("📦 Clasifica · ${(index + 1).coerceAtMost(payload.items.size)} de ${payload.items.size}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Toca la categoría a la que pertenece.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(10.dp))
        if (!completed && item != null) {
            BigChoiceCard(text = item.label, color = Color(0xFF8B75F0))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                payload.categories.forEach { cat ->
                    ChoiceChip(
                        text = cat,
                        selected = answers[item.label] == cat,
                        color = Color(0xFFFFB020),
                    ) {
                        answers = answers + (item.label to cat)
                        if (index < payload.items.size - 1) index += 1
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
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

/** Tarjeta grande centrada para mostrar un elemento a clasificar/predecir. Reutilizable. */
@Composable
fun BigChoiceCard(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = color.copy(alpha = 0.14f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

/** Chip grande tocable, usado como "canasta" de categoría o como opción. Reutilizable. */
@Composable
fun ChoiceChip(text: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) color else color.copy(alpha = 0.18f),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        )
    }
}

// ---------------------------------------------------------------------------
// BUILD — repisa en grilla de herramientas tocables, como armar una mochila.
// ---------------------------------------------------------------------------

@Composable
private fun BuildChallenge(dataJson: String, completed: Boolean, summary: String?, onSubmit: (Set<String>) -> Unit) {
    val payload = remember(dataJson) { challengeJson.decodeFromString(BuildPayload.serializer(), dataJson) }
    val allInstruments = remember(dataJson) { (payload.correctInstruments + payload.distractorInstruments).shuffled() }
    var selected by remember { mutableStateOf(setOf<String>()) }
    AppCard {
        Text("🎒 Arma tu mochila", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Toca lo que necesitas para este experimento.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        allInstruments.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { instrument ->
                    ToolTile(
                        label = instrument,
                        checked = instrument in selected,
                        enabled = !completed,
                        modifier = Modifier.weight(1f),
                    ) { checked -> selected = if (checked) selected + instrument else selected - instrument }
                }
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }
        if (!completed) {
            PrimaryButton(text = "Armar experimento", enabled = selected.isNotEmpty(), onClick = { onSubmit(selected) }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(summary ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ToolTile(label: String, checked: Boolean, enabled: Boolean, modifier: Modifier = Modifier, onToggle: (Boolean) -> Unit) {
    val glyphColor = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = modifier.clickable(enabled = enabled) { onToggle(!checked) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Canvas(modifier = Modifier.size(34.dp)) { drawToolGlyph(glyphColor) }
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

private fun DrawScope.drawToolGlyph(color: Color) {
    val w = size.width
    val h = size.height
    drawArc(color, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.26f, h * 0.02f), size = Size(w * 0.48f, h * 0.4f), style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))
    drawRoundRect(color, topLeft = Offset(w * 0.1f, h * 0.4f), size = Size(w * 0.8f, h * 0.55f), cornerRadius = CornerRadius(w * 0.1f), style = Stroke(width = w * 0.09f))
    drawLine(color, Offset(w * 0.5f, h * 0.52f), Offset(w * 0.5f, h * 0.68f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
}
