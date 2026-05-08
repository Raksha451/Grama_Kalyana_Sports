package com.gramakalyana.sports.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakalyana.sports.data.model.Match
import com.gramakalyana.sports.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MatchesViewModel(private val repository: SportsRepository = SportsRepository()) : ViewModel() {
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    private val _currentMatch = MutableStateFlow<Match?>(null)
    val currentMatch: StateFlow<Match?> = _currentMatch

    fun loadMatches(tournamentId: String) {
        viewModelScope.launch {
            repository.observeLiveMatches().collect { allMatches ->
                _matches.value = allMatches.filter { it.tournamentId == tournamentId }
            }
        }
    }

    fun loadAllMatches() {
        viewModelScope.launch {
            repository.observeLiveMatches().collect { allMatches ->
                _matches.value = allMatches
            }
        }
    }

    fun loadMatch(matchId: String) {
        viewModelScope.launch {
            repository.observeLiveMatches().collect { matches ->
                _currentMatch.value = matches.find { it.id == matchId }
            }
        }
    }

    fun createMatch(match: Match) {
        viewModelScope.launch {
            repository.createMatch(match)
        }
    }
}
