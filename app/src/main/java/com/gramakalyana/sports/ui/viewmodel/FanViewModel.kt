package com.gramakalyana.sports.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakalyana.sports.data.model.Match
import com.gramakalyana.sports.data.model.Player
import com.gramakalyana.sports.data.model.Tournament
import com.gramakalyana.sports.data.repository.SportsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FanViewModel(private val repository: SportsRepository = SportsRepository()) : ViewModel() {
    
    val liveMatches: StateFlow<List<Match>> = repository.observeLiveMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tournaments: StateFlow<List<Tournament>> = repository.getTournaments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val players: StateFlow<List<Player>> = repository.getAllPlayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
