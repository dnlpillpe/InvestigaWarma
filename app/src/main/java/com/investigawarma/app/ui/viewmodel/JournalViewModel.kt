package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investigawarma.app.data.repository.JournalRepository
import com.investigawarma.app.data.repository.PlayerRepository
import com.investigawarma.app.util.VoiceRecorderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecordingUiState(
    val isRecording: Boolean = false,
    val elapsedSeconds: Int = 0,
    val permissionDenied: Boolean = false,
)

class JournalViewModel(
    private val journalRepository: JournalRepository,
    private val voiceRecorderManager: VoiceRecorderManager,
    private val playerRepository: PlayerRepository,
) : ViewModel() {

    val entries = journalRepository.observeEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _recordingState = MutableStateFlow(RecordingUiState())
    val recordingState: StateFlow<RecordingUiState> = _recordingState.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    private val _playingEntryId = MutableStateFlow<Long?>(null)
    val playingEntryId: StateFlow<Long?> = _playingEntryId.asStateFlow()

    fun togglePlayback(journalId: Long) {
        if (_playingEntryId.value == journalId) {
            voiceRecorderManager.stopPlayback()
            _playingEntryId.value = null
            return
        }
        viewModelScope.launch {
            val voiceEntry = journalRepository.getVoiceEntry(journalId) ?: return@launch
            _playingEntryId.value = journalId
            voiceRecorderManager.play(voiceEntry.filePath) { _playingEntryId.value = null }
        }
    }

    fun addTextEntry(title: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch { journalRepository.addTextEntry(title, content, missionId = null) }
    }

    fun onPermissionDenied() {
        _recordingState.value = _recordingState.value.copy(permissionDenied = true)
    }

    /** Se llama únicamente tras confirmar el permiso RECORD_AUDIO concedido. */
    fun startRecording() {
        val file = voiceRecorderManager.startRecording()
        if (file == null) {
            _recordingState.value = _recordingState.value.copy(permissionDenied = true)
            return
        }
        _recordingState.value = RecordingUiState(isRecording = true, elapsedSeconds = 0)
        timerJob = viewModelScope.launch {
            while (_recordingState.value.isRecording && _recordingState.value.elapsedSeconds < 60) {
                kotlinx.coroutines.delay(1000)
                _recordingState.value = _recordingState.value.copy(
                    elapsedSeconds = _recordingState.value.elapsedSeconds + 1,
                )
            }
            if (_recordingState.value.isRecording) stopRecordingAndSave("Nota de voz")
        }
    }

    fun stopRecordingAndSave(title: String) {
        timerJob?.cancel()
        val result = voiceRecorderManager.stopRecording()
        _recordingState.value = RecordingUiState()
        if (result != null) {
            val (file, duration) = result
            viewModelScope.launch {
                journalRepository.addVoiceEntry(title, missionId = null, filePath = file.absolutePath, durationSeconds = duration)
            }
        }
    }

    fun cancelRecording() {
        timerJob?.cancel()
        voiceRecorderManager.cancelRecording()
        _recordingState.value = RecordingUiState()
    }

    fun playVoiceEntry(filePath: String, onCompletion: () -> Unit) {
        voiceRecorderManager.play(filePath, onCompletion)
    }

    fun stopPlayback() {
        voiceRecorderManager.stopPlayback()
    }

    override fun onCleared() {
        super.onCleared()
        voiceRecorderManager.stopPlayback()
        voiceRecorderManager.cancelRecording()
    }
}
