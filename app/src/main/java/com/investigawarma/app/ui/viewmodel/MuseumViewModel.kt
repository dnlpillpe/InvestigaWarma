package com.investigawarma.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investigawarma.app.data.repository.CollectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MuseumViewModel(private val collectionRepository: CollectionRepository) : ViewModel() {

    val collectionItems = collectionRepository.observeCollectionItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges = collectionRepository.observeBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { collectionRepository.refreshBadges() }
    }
}
