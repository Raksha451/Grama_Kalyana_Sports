package com.gramakalyana.sports.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakalyana.sports.data.model.Team
import com.gramakalyana.sports.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamsViewModel(private val repository: SportsRepository = SportsRepository()) : ViewModel() {
    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    fun loadTeams(tournamentId: String) {
        viewModelScope.launch {
            repository.getTeams(tournamentId).collect {
                _teams.value = it
            }
        }
    }

    fun addTeam(team: Team) {
        viewModelScope.launch {
            repository.addTeam(team)
        }
    }
}
