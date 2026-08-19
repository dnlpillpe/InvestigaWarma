package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investigawarma.app.data.local.entity.ChallengeEntity
import com.investigawarma.app.data.local.entity.ExperimentParameterEntity
import com.investigawarma.app.data.local.entity.MissionStepEntity
import com.investigawarma.app.data.local.entity.ScientificMissionEntity
import com.investigawarma.app.data.repository.ChallengeRepository
import com.investigawarma.app.data.repository.CollectionRepository
import com.investigawarma.app.data.repository.ExperimentRepository
import com.investigawarma.app.data.repository.JournalRepository
import com.investigawarma.app.data.repository.MissionRepository
import com.investigawarma.app.data.repository.PlayerRepository
import com.investigawarma.app.domain.logic.ChallengeEvaluator
import com.investigawarma.app.domain.model.BuildPayload
import com.investigawarma.app.domain.model.ClassifyPayload
import com.investigawarma.app.domain.model.DetectivePayload
import com.investigawarma.app.domain.model.MissionExperimentContent
import com.investigawarma.app.domain.model.MissionStepType
import com.investigawarma.app.domain.model.OrderPayload
import com.investigawarma.app.domain.model.PatternPayload
import com.investigawarma.app.domain.model.SimulatorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class MissionUiState(
    val mission: ScientificMissionEntity? = null,
    val steps: List<MissionStepEntity> = emptyList(),
    val stepIndex: Int = 0,
    val isLoading: Boolean = true,
    val irisMessage: String = "",
    val questionConnector: String? = null,
    val questionText: String = "",
    val hypothesisVariable: String = "",
    val hypothesisResult: String = "",
    val hypothesisExplanation: String = "",
    val hypothesisFeedback: String? = null,
    val hypothesisAttempts: Int = 0,
    val hypothesisValid: Boolean = false,
    val experimentContent: MissionExperimentContent? = null,
    val experimentParameters: List<ExperimentParameterEntity> = emptyList(),
    val experimentValues: Map<String, Float> = emptyMap(),
    val experimentResultSummary: String? = null,
    val experimentScore: Float = 0f,
    val experimentCompleted: Boolean = false,
    val challenge: ChallengeEntity? = null,
    val analyzeNote: String = "",
    val missionCompleted: Boolean = false,
    val xpAwarded: Int = 0,
    val starsAwarded: Int = 0,
    val finalScore: Float = 0f,
)

class MissionViewModel(
    private val missionRepository: MissionRepository,
    private val experimentRepository: ExperimentRepository,
    private val challengeRepository: ChallengeRepository,
    private val playerRepository: PlayerRepository,
    private val collectionRepository: CollectionRepository,
    private val journalRepository: JournalRepository,
    private val soundHelper: com.investigawarma.app.util.SoundHelper,
    private val hapticsHelper: com.investigawarma.app.util.HapticsHelper,
) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true }
    private val _uiState = MutableStateFlow(MissionUiState())
    val uiState: StateFlow<MissionUiState> = _uiState.asStateFlow()

    fun loadMission(missionId: String) {
        viewModelScope.launch {
            val mission = missionRepository.getMission(missionId) ?: return@launch
            missionRepository.startMission(missionId)
            val steps = missionRepository.observeSteps(missionId)
            steps.collect { stepList ->
                if (_uiState.value.mission == null) {
                    _uiState.value = MissionUiState(
                        mission = mission,
                        steps = stepList.sortedBy { it.orderIndex },
                        isLoading = false,
                        irisMessage = "¡Vamos! Encontré algo interesante por aquí. ¿Exploramos juntos?",
                    )
                    loadCurrentStepExtras()
                }
            }
        }
    }

    private fun currentStep(): MissionStepEntity? =
        _uiState.value.steps.getOrNull(_uiState.value.stepIndex)

    private fun loadCurrentStepExtras() {
        val step = currentStep() ?: return
        if (step.stepType == MissionStepType.EXPERIMENT.name) {
            viewModelScope.launch {
                val content = runCatching {
                    json.decodeFromString(MissionExperimentContent.serializer(), step.contentJson)
                }.getOrNull()
                _uiState.value = _uiState.value.copy(experimentContent = content)
                when (content?.kind) {
                    "experiment" -> {
                        val expId = content.experimentId ?: return@launch
                        val params = experimentRepository.getParameters(expId)
                        _uiState.value = _uiState.value.copy(
                            experimentParameters = params,
                            experimentValues = params.associate { it.name to it.defaultValue },
                        )
                    }
                    "challenge" -> {
                        val chId = content.challengeId ?: return@launch
                        val challenge = challengeRepository.getChallenge(chId)
                        _uiState.value = _uiState.value.copy(challenge = challenge)
                    }
                }
            }
        }
    }

    fun updateExperimentValue(name: String, value: Float) {
        val updated = _uiState.value.experimentValues.toMutableMap().apply { put(name, value) }
        _uiState.value = _uiState.value.copy(experimentValues = updated)
    }

    fun runSimulatorExperiment() {
        val content = _uiState.value.experimentContent ?: return
        val expId = content.experimentId ?: return
        viewModelScope.launch {
            val experiment = experimentRepository.getExperiment(expId) ?: return@launch
            val simulatorType = runCatching { SimulatorType.valueOf(experiment.simulatorType) }
                .getOrDefault(SimulatorType.NONE)
            val result = experimentRepository.runExperiment(expId, simulatorType, _uiState.value.experimentValues)
            _uiState.value = _uiState.value.copy(
                experimentResultSummary = result.outcomeSummary,
                experimentScore = result.outcomeScore,
                experimentCompleted = true,
            )
            soundHelper.playSuccess(true)
            hapticsHelper.success(true)
        }
    }

    fun submitCompareAnswer(selectedIndices: Set<Int>) {
        val content = _uiState.value.experimentContent ?: return
        val correct = content.correctIndices?.toSet() ?: emptySet()
        val score = if (correct.isEmpty()) 0f else {
            val hits = selectedIndices.intersect(correct).size
            val misses = selectedIndices.subtract(correct).size
            ((hits - misses).toFloat() / correct.size).coerceIn(0f, 1f)
        }
        finishExperimentStep(score, if (score >= 0.99f) "¡Encontraste todas las diferencias reales!" else "Buen intento. Revisa lo que observaste.")
    }

    fun submitDragDropAnswer(matches: Map<String, String>) {
        val content = _uiState.value.experimentContent ?: return
        val pairs = content.pairs ?: emptyList()
        if (pairs.isEmpty()) return
        val correctCount = pairs.count { matches[it.label] == it.definition }
        val score = correctCount.toFloat() / pairs.size
        finishExperimentStep(score, "Emparejaste $correctCount de ${pairs.size} correctamente.")
    }

    fun submitPredictAnswer(selectedIndex: Int) {
        val content = _uiState.value.experimentContent ?: return
        val correct = content.correctIndex ?: return
        val score = if (selectedIndex == correct) 1f else 0.3f
        finishExperimentStep(score, if (score == 1f) "¡Tu predicción fue correcta!" else "No era lo que esperabas, pero así aprende un investigador.")
    }

    fun submitDetectiveAnswer(chosenIndex: Int) = viewModelScope.launch {
        val challenge = _uiState.value.challenge ?: return@launch
        val payload = json.decodeFromString(DetectivePayload.serializer(), challenge.dataJson)
        val success = ChallengeEvaluator.evaluateDetective(payload.errorIndex, chosenIndex)
        challengeRepository.recordAttempt(challenge.id, success, if (success) 1f else 0f)
        finishExperimentStep(if (success) 1f else 0.3f, if (success) "¡Encontraste el error!" else "No era ese, pero sigue investigando.")
    }

    fun submitOrderAnswer(order: List<String>) = viewModelScope.launch {
        val challenge = _uiState.value.challenge ?: return@launch
        val payload = json.decodeFromString(OrderPayload.serializer(), challenge.dataJson)
        val success = ChallengeEvaluator.evaluateOrder(payload, order)
        challengeRepository.recordAttempt(challenge.id, success, if (success) 1f else 0f)
        finishExperimentStep(if (success) 1f else 0.3f, if (success) "¡Orden correcto!" else "El orden no es ese todavía.")
    }

    fun submitPatternAnswer(answer: Int) = viewModelScope.launch {
        val challenge = _uiState.value.challenge ?: return@launch
        val payload = json.decodeFromString(PatternPayload.serializer(), challenge.dataJson)
        val success = ChallengeEvaluator.evaluatePattern(payload, answer)
        challengeRepository.recordAttempt(challenge.id, success, if (success) 1f else 0f)
        finishExperimentStep(if (success) 1f else 0.3f, if (success) "¡Descubriste el patrón!" else "Ese no es el patrón. Observa de nuevo.")
    }

    fun submitClassifyAnswer(answers: Map<String, String>) = viewModelScope.launch {
        val challenge = _uiState.value.challenge ?: return@launch
        val payload = json.decodeFromString(ClassifyPayload.serializer(), challenge.dataJson)
        val outcome = ChallengeEvaluator.evaluateClassify(payload.items, answers)
        challengeRepository.recordAttempt(challenge.id, outcome.success, outcome.accuracy)
        finishExperimentStep(outcome.accuracy, "Clasificaste correctamente ${(outcome.accuracy * 100).toInt()}%.")
    }

    fun submitBuildAnswer(selected: Set<String>) = viewModelScope.launch {
        val challenge = _uiState.value.challenge ?: return@launch
        val payload = json.decodeFromString(BuildPayload.serializer(), challenge.dataJson)
        val success = ChallengeEvaluator.evaluateBuild(payload, selected)
        challengeRepository.recordAttempt(challenge.id, success, if (success) 1f else 0f)
        finishExperimentStep(if (success) 1f else 0.4f, if (success) "¡Elegiste los instrumentos correctos!" else "Faltan o sobran instrumentos.")
    }

    fun submitFindSpotAnswer(foundCount: Int, total: Int) {
        val score = if (total <= 0) 1f else (foundCount.toFloat() / total).coerceIn(0f, 1f)
        val message = if (score >= 0.99f) "¡Encontraste todo! Buen ojo de explorador." else "Encontraste $foundCount de $total. ¡Sigue mirando!"
        finishExperimentStep(score, message)
    }

    private fun finishExperimentStep(score: Float, message: String) {
        _uiState.value = _uiState.value.copy(
            experimentScore = score,
            experimentResultSummary = message,
            experimentCompleted = true,
        )
        soundHelper.playSuccess(true)
        hapticsHelper.success(true)
    }

    fun updateQuestionDraft(connector: String?, text: String) {
        _uiState.value = _uiState.value.copy(questionConnector = connector, questionText = text)
    }

    fun submitHypothesis(variable: String, result: String, explanation: String) {
        viewModelScope.launch {
            val mission = _uiState.value.mission ?: return@launch
            val validation = missionRepository.submitHypothesis(mission.id, variable, result, explanation)
            _uiState.value = _uiState.value.copy(
                hypothesisVariable = variable,
                hypothesisResult = result,
                hypothesisExplanation = explanation,
                hypothesisFeedback = validation.reason,
                hypothesisValid = validation.isValid,
                hypothesisAttempts = _uiState.value.hypothesisAttempts + 1,
            )
            if (validation.isValid) {
                soundHelper.playSuccess(true)
            } else {
                soundHelper.playError(true)
                hapticsHelper.error(true)
            }
        }
    }

    fun canAdvanceHypothesis(): Boolean =
        _uiState.value.hypothesisValid || _uiState.value.hypothesisAttempts >= 2

    fun updateAnalyzeNote(note: String) {
        _uiState.value = _uiState.value.copy(analyzeNote = note)
    }

    fun saveAnalyzeNoteToJournal() {
        val note = _uiState.value.analyzeNote.trim()
        val mission = _uiState.value.mission ?: return
        if (note.isEmpty()) return
        viewModelScope.launch {
            journalRepository.addTextEntry(title = mission.title, content = note, missionId = mission.id)
        }
    }

    fun goToNextStep() {
        val state = _uiState.value
        if (state.stepIndex < state.steps.size - 1) {
            _uiState.value = state.copy(stepIndex = state.stepIndex + 1)
            loadCurrentStepExtras()
        } else {
            completeMission()
        }
    }

    fun goToPreviousStep() {
        val state = _uiState.value
        if (state.stepIndex > 0) {
            _uiState.value = state.copy(stepIndex = state.stepIndex - 1)
        }
    }

    private fun completeMission() {
        val mission = _uiState.value.mission ?: return
        viewModelScope.launch {
            val hypothesisScore = if (_uiState.value.hypothesisValid) 1f else 0.5f
            val experimentScore = _uiState.value.experimentScore.takeIf { _uiState.value.experimentCompleted } ?: 0.5f
            val finalScore = ((hypothesisScore * 0.3f) + (experimentScore * 0.7f)).coerceIn(0f, 1f)

            missionRepository.completeMission(mission.id, finalScore)
            playerRepository.addXp(mission.xpReward)
            collectionRepository.refreshBadges()

            _uiState.value = _uiState.value.copy(
                missionCompleted = true,
                xpAwarded = mission.xpReward,
                starsAwarded = mission.starReward,
                finalScore = finalScore,
            )
            hapticsHelper.unlock(true)
            soundHelper.playUnlock(true)
        }
    }
}
