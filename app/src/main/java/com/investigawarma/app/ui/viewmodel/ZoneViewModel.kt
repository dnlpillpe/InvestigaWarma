package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investigawarma.app.data.local.entity.ChallengeEntity
import com.investigawarma.app.data.local.entity.MissionProgressEntity
import com.investigawarma.app.data.local.entity.ScientificMissionEntity
import com.investigawarma.app.data.repository.ChallengeRepository
import com.investigawarma.app.data.repository.MissionRepository
import com.investigawarma.app.domain.model.Zone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class MissionWithProgress(val mission: ScientificMissionEntity, val progress: MissionProgressEntity?)

data class ZoneUiState(
    val zone: Zone = Zone.SALA_OBSERVACION,
    val missions: List<MissionWithProgress> = emptyList(),
    val challenges: List<ChallengeEntity> = emptyList(),
    val isLoading: Boolean = true,
)

/** Zone se pasa vía factory manual (no SavedStateHandle) para simplicidad; ver setZone(). */
class ZoneViewModel(
    private val missionRepository: MissionRepository,
    private val challengeRepository: ChallengeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ZoneUiState())
    val uiState: StateFlow<ZoneUiState> = _uiState.asStateFlow()

    fun loadZone(zone: Zone) {
        if (_uiState.value.zone == zone && _uiState.value.missions.isNotEmpty()) return
        combine(
            missionRepository.observeMissionsByZone(zone),
            missionRepository.observeAllProgress(),
            challengeRepository.observeByZone(zone.name),
        ) { missions, allProgress, challenges ->
            val progressByMission = allProgress.associateBy { it.missionId }
            ZoneUiState(
                zone = zone,
                missions = missions.map { MissionWithProgress(it, progressByMission[it.id]) },
                challenges = challenges,
                isLoading = false,
            )
        }.onEach { _uiState.value = it }.launchIn(viewModelScope)
    }
}
