package com.gramakalyana.sports.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakalyana.sports.data.model.Player
import com.gramakalyana.sports.data.model.SportType
import com.gramakalyana.sports.data.model.Team
import com.gramakalyana.sports.data.model.Tournament
import com.gramakalyana.sports.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerProfileViewModel(private val repository: SportsRepository = SportsRepository()) : ViewModel() {
    private val _player = MutableStateFlow<Player?>(null)
    val player: StateFlow<Player?> = _player

    private val _sportType = MutableStateFlow<SportType?>(null)
    val sportType: StateFlow<SportType?> = _sportType

    private val _team = MutableStateFlow<Team?>(null)
    val team: StateFlow<Team?> = _team

    private val _tournament = MutableStateFlow<Tournament?>(null)
    val tournament: StateFlow<Tournament?> = _tournament

    fun loadPlayerProfile(playerId: String) {
        viewModelScope.launch {
            repository.getPlayer(playerId).collect { player ->
                _player.value = player
                player?.teamId?.let { teamId ->
                    if (teamId.isNotBlank()) {
                        repository.getTeam(teamId).collect { team ->
                            _team.value = team
                            team?.tournamentId?.let { tournamentId ->
                                repository.getTournament(tournamentId).collect { tournament ->
                                    _tournament.value = tournament
                                    _sportType.value = tournament?.sportType
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
