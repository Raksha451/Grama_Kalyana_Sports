package com.gramakalyana.sports.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakalyana.sports.data.model.Tournament
import com.gramakalyana.sports.data.repository.SportsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TournamentViewModel(private val repository: SportsRepository = SportsRepository()) : ViewModel() {
    
    val tournaments: StateFlow<List<Tournament>> = repository.getTournaments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createTournament(tournament: Tournament) {
        viewModelScope.launch {
            repository.createTournament(tournament)
        }
    }
}
