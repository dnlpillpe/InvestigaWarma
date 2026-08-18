package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investigawarma.app.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(private val statsRepository: StatsRepository) : ViewModel() {

    private val _stats = MutableStateFlow<StatsRepository.PlayerStats?>(null)
    val stats: StateFlow<StatsRepository.PlayerStats?> = _stats.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _stats.value = statsRepository.computeStats()
        }
    }
}
