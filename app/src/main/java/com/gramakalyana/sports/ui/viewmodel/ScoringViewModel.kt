package com.gramakalyana.sports.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakalyana.sports.data.model.*
import com.gramakalyana.sports.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScoringViewModel(private val repository: SportsRepository = SportsRepository()) : ViewModel() {
    private val _match = MutableStateFlow<Match?>(null)
    val match: StateFlow<Match?> = _match

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private val _teamA = MutableStateFlow<Team?>(null)
    val teamA: StateFlow<Team?> = _teamA

    private val _teamB = MutableStateFlow<Team?>(null)
    val teamB: StateFlow<Team?> = _teamB

    private val _tournament = MutableStateFlow<Tournament?>(null)
    val tournament: StateFlow<Tournament?> = _tournament

    fun loadMatch(matchId: String) {
        viewModelScope.launch {
            repository.observeMatch(matchId).collect { updatedMatch ->
                _match.value = updatedMatch
                updatedMatch?.let { m ->
                    loadTeams(m.teamAId, m.teamBId)
                    loadTournament(m.tournamentId)
                }
            }
        }
        loadAllPlayers()
    }

    private fun loadTeams(idA: String, idB: String) {
        viewModelScope.launch {
            repository.getTeam(idA).collect { _teamA.value = it }
        }
        viewModelScope.launch {
            repository.getTeam(idB).collect { _teamB.value = it }
        }
    }

    private fun loadTournament(tournamentId: String) {
        viewModelScope.launch {
            repository.getTournament(tournamentId).collect { _tournament.value = it }
        }
    }

    private fun loadAllPlayers() {
        viewModelScope.launch {
            repository.getAllPlayers().collect {
                _players.value = it
            }
        }
    }

    fun startMatch(matchId: String, sportType: String) {
        viewModelScope.launch {
            repository.updateMatchScore(matchId, Score(type = sportType))
            repository.updateMatchStatus(matchId, MatchStatus.LIVE)
        }
    }

    fun updateScore(matchId: String, newScore: Score) {
        viewModelScope.launch {
            repository.updateMatchScore(matchId, newScore)
            // Ensure status is LIVE when updating scores
            if (_match.value?.status != MatchStatus.LIVE) {
                repository.updateMatchStatus(matchId, MatchStatus.LIVE)
            }
        }
    }

    fun updateManOfTheMatch(matchId: String, playerId: String) {
        viewModelScope.launch {
            repository.updateManOfTheMatch(matchId, playerId)
        }
    }

    fun updatePlayerStat(playerId: String, statKey: String, increment: Int) {
        viewModelScope.launch {
            repository.incrementPlayerStat(playerId, statKey, increment)
        }
    }

    fun finishMatch(matchId: String, winnerId: String?) {
        viewModelScope.launch {
            repository.completeMatch(matchId, winnerId)
        }
    }
}
