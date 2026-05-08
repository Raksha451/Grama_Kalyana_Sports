package com.gramakalyana.sports.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakalyana.sports.data.model.Player
import com.gramakalyana.sports.data.repository.SportsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayersViewModel(private val repository: SportsRepository = SportsRepository()) : ViewModel() {
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private var fetchJob: Job? = null

    fun loadPlayers(teamId: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            val flow = if (teamId == "global") {
                repository.getAllPlayers()
            } else {
                repository.getPlayers(teamId)
            }
            flow.collect {
                _players.value = it
            }
        }
    }

    fun addPlayer(player: Player, teamId: String) {
        viewModelScope.launch {
            repository.addPlayer(player, teamId)
        }
    }
}
