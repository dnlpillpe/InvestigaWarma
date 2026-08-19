package com.investigawarma.app.ui.screens

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.data.local.entity.ScientificMissionEntity
import com.investigawarma.app.domain.model.MissionStepType
import com.investigawarma.app.domain.model.Zone
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.IllustrationCatalog
import com.investigawarma.app.ui.components.IrisExpression
import com.investigawarma.app.ui.components.IrisMessageBubble
import com.investigawarma.app.ui.components.PrimaryButton
import com.investigawarma.app.ui.components.SceneIllustration
import com.investigawarma.app.ui.components.SecondaryButton
import com.investigawarma.app.ui.components.StarRow
import com.investigawarma.app.ui.theme.ZoneBio
import com.investigawarma.app.ui.theme.ZoneDatos
import com.investigawarma.app.ui.theme.ZoneLaboratorio
import com.investigawarma.app.ui.theme.ZoneMuseo
import com.investigawarma.app.ui.theme.ZoneObservacion
import com.investigawarma.app.ui.theme.ZonePlaneta
import com.investigawarma.app.ui.viewmodel.MissionViewModel
import com.investigawarma.app.ui.viewmodel.ViewModelFactory

/** Color de acento por zona, usado para teñir las ilustraciones de misión. */
fun zoneTint(zone: Zone): Color = when (zone) {
    Zone.SALA_OBSERVACION -> ZoneObservacion
    Zone.LABORATORIO_EXPERIMENTAL -> ZoneLaboratorio
    Zone.BIODESCUBRIMIENTO -> ZoneBio
    Zone.PLANETA_TIERRA -> ZonePlaneta
    Zone.CENTRO_DE_DATOS -> ZoneDatos
    Zone.MUSEO_CIENTIFICO -> ZoneMuseo
}

@Composable
fun MissionScreen(
    factory: ViewModelFactory,
    missionId: String,
    onBack: () -> Unit,
    onMissionFinished: () -> Unit,
) {
    val viewModel: MissionViewModel = viewModel(factory = factory)
    LaunchedEffect(missionId) { viewModel.loadMission(missionId) }
    val state by viewModel.uiState.collectAsState()

    if (state.mission == null || state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando misión…")
        }
        return
    }

    if (state.missionCompleted) {
        DiscoveryCelebration(
            mission = state.mission!!,
            title = state.mission?.title.orEmpty(),
            xp = state.xpAwarded,
            stars = state.starsAwarded,
            onContinue = onMissionFinished,
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás") }
            Text(state.mission?.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
        }
        StepDots(total = state.steps.size, current = state.stepIndex)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            val step = state.steps.getOrNull(state.stepIndex)
            IrisMessageBubble(message = state.irisMessage, expression = IrisExpression.EXPLICANDO)
            Spacer(Modifier.height(12.dp))
            when (step?.stepType) {
                MissionStepType.OBSERVE.name -> ObserveStep(mission = state.mission!!, promptText = step.promptText)
                MissionStepType.QUESTION.name -> QuestionStep(promptText = step.promptText, viewModel = viewModel, state = state)
                MissionStepType.HYPOTHESIS.name -> HypothesisStep(viewModel = viewModel, state = state)
                MissionStepType.EXPERIMENT.name -> ExperimentStep(viewModel = viewModel, state = state)
                MissionStepType.ANALYZE.name -> AnalyzeStep(promptText = step.promptText, viewModel = viewModel, state = state)
                MissionStepType.DISCOVERY.name -> DiscoveryPreview(mission = state.mission!!, promptText = step.promptText)
                else -> {}
            }
        }

        MissionNavBar(state = state, onNext = { viewModel.goToNextStep() }, onPrevious = { viewModel.goToPreviousStep() })
    }
}

@Composable
private fun StepDots(total: Int, current: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            Surface(
                shape = CircleShape,
                color = if (i <= current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                modifier = Modifier.height(6.dp).weight(1f),
            ) {}
        }
    }
}

@Composable
private fun MissionNavBar(
    state: com.investigawarma.app.ui.viewmodel.MissionUiState,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    val step = state.steps.getOrNull(state.stepIndex)
    val canAdvance = when (step?.stepType) {
        MissionStepType.HYPOTHESIS.name -> state.hypothesisValid || state.hypothesisAttempts >= 2
        MissionStepType.EXPERIMENT.name -> state.experimentCompleted
        else -> true
    }
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        if (state.stepIndex > 0) {
            SecondaryButton(text = "Atrás", onClick = onPrevious, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
        }
        PrimaryButton(
            text = if (state.stepIndex == state.steps.size - 1) "Descubrir" else "Continuar",
            onClick = onNext,
            enabled = canAdvance,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Tarjeta grande tocable para elegir una opción (pregunta, hipótesis, etc.). Compartida entre pasos. */
@Composable
fun OptionCard(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Text(text, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ObserveStep(mission: ScientificMissionEntity, promptText: String) {
    val tint = runCatching { Zone.valueOf(mission.zone) }.getOrNull()?.let { zoneTint(it) }
        ?: MaterialTheme.colorScheme.primary
    var revealed by remember(mission.id) { mutableStateOf(false) }
    AppCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable(enabled = !revealed) { revealed = true },
        ) {
            SceneIllustration(
                key = IllustrationCatalog.forMission(mission.id),
                tint = tint,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (revealed) 1f else 0.35f),
            )
            if (!revealed) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("👆", style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            if (revealed) promptText else "Toca la escena para explorarla.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

/** Preguntas completas y con sentido propio (no se arman pegando una palabra clave). */
private val QUESTION_TEMPLATES = listOf(
    "¿Por qué crees que pasó esto?" to "¿Qué pasaría si lo intentas de nuevo?",
    "¿Por qué pasará eso?" to "¿Qué crees que cambiaría si lo repites?",
    "¿Por qué te parece que es así?" to "¿Qué pasaría si algo fuera distinto?",
    "¿Por qué se comportará así?" to "¿Qué pasaría si sigues investigando?",
)

@Composable
private fun QuestionStep(
    promptText: String,
    viewModel: MissionViewModel,
    state: com.investigawarma.app.ui.viewmodel.MissionUiState,
) {
    val missionNumber = state.mission?.id?.drop(1)?.toIntOrNull() ?: 1
    val (why, whatIf) = QUESTION_TEMPLATES[(missionNumber - 1) % QUESTION_TEMPLATES.size]
    val templates = listOf(
        why to "¿Por qué?",
        whatIf to "¿Qué pasa si?",
    )
    var customMode by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(state.questionText) }

    AppCard {
        Text(promptText, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(12.dp))
        if (!customMode) {
            templates.forEach { (question, connector) ->
                OptionCard(text = question, selected = state.questionText == question) {
                    text = question
                    viewModel.updateQuestionDraft(connector, question)
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(4.dp))
            SecondaryButton(text = "Escribir mi propia pregunta", onClick = { customMode = true }, modifier = Modifier.fillMaxWidth())
        } else {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it; viewModel.updateQuestionDraft("¿Por qué?", it) },
                label = { Text("Tu pregunta") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
