package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.investigawarma.app.AppContainer

/**
 * Factory manual de ViewModels a partir de AppContainer (inyección manual,
 * sin frameworks adicionales, tal como recomienda la especificación maestra).
 */
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.missionRepository, container.playerRepository, container.collectionRepository) as T
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(container.playerRepository) as T
            modelClass.isAssignableFrom(ZoneViewModel::class.java) ->
                ZoneViewModel(container.missionRepository, container.challengeRepository) as T
            modelClass.isAssignableFrom(MissionViewModel::class.java) ->
                MissionViewModel(
                    container.missionRepository,
                    container.experimentRepository,
                    container.challengeRepository,
                    container.playerRepository,
                    container.collectionRepository,
                    container.journalRepository,
                    container.soundHelper,
                    container.hapticsHelper,
                ) as T
            modelClass.isAssignableFrom(JournalViewModel::class.java) ->
                JournalViewModel(container.journalRepository, container.voiceRecorderManager, container.playerRepository) as T
            modelClass.isAssignableFrom(MuseumViewModel::class.java) ->
                MuseumViewModel(container.collectionRepository) as T
            modelClass.isAssignableFrom(StatsViewModel::class.java) ->
                StatsViewModel(container.statsRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(container.playerRepository) as T
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
