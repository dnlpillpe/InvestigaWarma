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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investigawarma.app.domain.model.MissionStepType
import com.investigawarma.app.ui.components.AppCard
import com.investigawarma.app.ui.components.IrisExpression
import com.investigawarma.app.ui.components.IrisMessageBubble
import com.investigawarma.app.ui.components.PrimaryButton
import com.investigawarma.app.ui.components.SecondaryButton
import com.investigawarma.app.ui.components.StarRow
import com.investigawarma.app.ui.viewmodel.MissionViewModel
import com.investigawarma.app.ui.viewmodel.ViewModelFactory

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
                MissionStepType.OBSERVE.name -> ObserveStep(promptText = step.promptText)
                MissionStepType.QUESTION.name -> QuestionStep(promptText = step.promptText, viewModel = viewModel, state = state)
                MissionStepType.HYPOTHESIS.name -> HypothesisStep(viewModel = viewModel, state = state)
                MissionStepType.EXPERIMENT.name -> ExperimentStep(viewModel = viewModel, state = state)
                MissionStepType.ANALYZE.name -> AnalyzeStep(promptText = step.promptText, viewModel = viewModel, state = state)
                MissionStepType.DISCOVERY.name -> DiscoveryPreview(promptText = step.promptText)
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

@Composable
private fun ObserveStep(promptText: String) {
    AppCard {
        Text(promptText, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun QuestionStep(
    promptText: String,
    viewModel: MissionViewModel,
    state: com.investigawarma.app.ui.viewmodel.MissionUiState,
) {
    val connectors = listOf("¿Por qué?", "¿Qué pasa si?")
    var connector by remember { mutableStateOf(state.questionConnector ?: connectors.first()) }
    var text by remember { mutableStateOf(state.questionText) }

    AppCard {
        Text(promptText, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(12.dp))
        Text("Elige un conector:", style = MaterialTheme.typography.labelLarge)
        Row(modifier = Modifier.padding(top = 4.dp)) {
            connectors.forEach { c ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (c == connector) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .then(Modifier),
                ) {
                    Text(
                        c,
                        modifier = Modifier
                            .clickable { connector = c; viewModel.updateQuestionDraft(connector, text) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        color = if (c == connector) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it; viewModel.updateQuestionDraft(connector, it) },
            label = { Text("Completa tu pregunta científica") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
