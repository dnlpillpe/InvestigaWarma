package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investigawarma.app.data.local.entity.PlayerProfileEntity
import com.investigawarma.app.data.repository.CollectionRepository
import com.investigawarma.app.data.repository.MissionRepository
import com.investigawarma.app.data.repository.PlayerRepository
import com.investigawarma.app.domain.logic.LevelCalculator
import com.investigawarma.app.domain.model.Zone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ZoneSummary(
    val zone: Zone,
    val completed: Int,
    val total: Int,
    val isUnlocked: Boolean,
)

data class HomeUiState(
    val profile: PlayerProfileEntity? = null,
    val levelName: String = "",
    val levelProgressPercent: Int = 0,
    val zones: List<ZoneSummary> = emptyList(),
    val isLoading: Boolean = true,
)

class HomeViewModel(
    private val missionRepository: MissionRepository,
    private val playerRepository: PlayerRepository,
    private val collectionRepository: CollectionRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        playerRepository.observeProfile(),
        missionRepository.observeAllMissions(),
        missionRepository.observeAllProgress(),
    ) { profile, missions, progress ->
        val progressByMission = progress.associateBy { it.missionId }
        val zoneSummaries = Zone.entries.map { zone ->
            val zoneMissions = missions.filter { it.zone == zone.name }
            val completed = zoneMissions.count {
                val status = progressByMission[it.id]?.status
                status == "COMPLETED" || status == "MASTERED"
            }
            val unlocked = zoneMissions.isEmpty() || zoneMissions.any {
                progressByMission[it.id]?.status != "LOCKED"
            }
            ZoneSummary(zone, completed, zoneMissions.size, unlocked)
        }
        val xp = profile?.xp ?: 0
        HomeUiState(
            profile = profile,
            levelName = LevelCalculator.levelFor(xp).displayName,
            levelProgressPercent = LevelCalculator.progressToNextLevel(xp),
            zones = zoneSummaries,
            isLoading = false,
        )
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        // Al entrar al mapa, se refrescan insignias/coleccionables por si hubo cambios pendientes.
        viewModelScope.launch { collectionRepository.refreshBadges() }
    }
}
