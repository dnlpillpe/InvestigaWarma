package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investigawarma.app.data.repository.PlayerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val playerRepository: PlayerRepository) : ViewModel() {

    val profile = playerRepository.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setSoundEnabled(enabled: Boolean) = viewModelScope.launch { playerRepository.setSoundEnabled(enabled) }

    fun setHapticsEnabled(enabled: Boolean) = viewModelScope.launch { playerRepository.setHapticsEnabled(enabled) }

    fun resetProgress(onDone: () -> Unit) = viewModelScope.launch {
        playerRepository.resetProgress()
        onDone()
    }
}
