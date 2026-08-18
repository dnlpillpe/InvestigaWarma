package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investigawarma.app.data.repository.PlayerRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(private val playerRepository: PlayerRepository) : ViewModel() {

    /** Los 8 avatares disponibles se identifican por índice (0..7); el dibujo vive en la UI. */
    val avatarCount = 8

    fun finishOnboarding(alias: String, avatarId: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            playerRepository.createProfile(alias, avatarId.coerceIn(0, avatarCount - 1))
            playerRepository.completeOnboarding()
            onDone()
        }
    }
}
