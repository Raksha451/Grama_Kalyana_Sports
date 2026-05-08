package com.gramakalyana.sports.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gramakalyana.sports.GramaKalyanaApplication
import com.gramakalyana.sports.data.local.*
import com.gramakalyana.sports.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SportsRepository(private val localDb: AppDatabase = GramaKalyanaApplication.instance.database) {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Tournament Methods
    suspend fun createTournament(tournament: Tournament) {
        database.child("tournaments").child(tournament.id).setValue(tournament).await()
        localDb.tournamentDao().insertTournament(
            TournamentEntity(
                id = tournament.id,
                name = tournament.name,
                sportType = tournament.sportType.name,
                venue = tournament.venue,
                startDate = tournament.startDate,
                organizerId = tournament.organizerId
            )
        )
    }

    fun getTournaments(): Flow<List<Tournament>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Tournament::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.child("tournaments").addValueEventListener(listener)
        awaitClose { database.child("tournaments").removeEventListener(listener) }
    }

    fun getTournament(tournamentId: String): Flow<Tournament?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Tournament::class.java))
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.child("tournaments").child(tournamentId).addValueEventListener(listener)
        awaitClose { database.child("tournaments").child(tournamentId).removeEventListener(listener) }
    }

    // Team Methods
    suspend fun addTeam(team: Team) {
        database.child("teams").child(team.id).setValue(team).await()
        localDb.teamDao().insertTeam(
            TeamEntity(
                id = team.id,
                name = team.name,
                villageName = "",
                tournamentId = team.tournamentId
            )
        )
    }

    fun getTeams(tournamentId: String): Flow<List<Team>> = callbackFlow {
        val query = database.child("teams").orderByChild("tournamentId").equalTo(tournamentId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Team::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getTeam(teamId: String): Flow<Team?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Team::class.java))
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.child("teams").child(teamId).addValueEventListener(listener)
        awaitClose { database.child("teams").child(teamId).removeEventListener(listener) }
    }

    // Player Methods
    suspend fun addPlayer(player: Player, teamId: String) {
        val playerWithTeam = player.copy(teamId = teamId)
        database.child("players").child(player.id).setValue(playerWithTeam).await()
        
        // Update Team's player ID list to keep count accurate
        val teamRef = database.child("teams").child(teamId)
        val snapshot = teamRef.get().await()
        val team = snapshot.getValue(Team::class.java)
        if (team != null) {
            val updatedPlayers = team.players.toMutableList()
            if (!updatedPlayers.contains(player.id)) {
                updatedPlayers.add(player.id)
                teamRef.child("players").setValue(updatedPlayers).await()
            }
        }
        
        localDb.playerDao().insertPlayer(
            PlayerEntity(
                id = player.id,
                name = player.name,
                teamId = teamId,
                jerseyNumber = player.jerseyNumber?.toString() ?: "",
                position = player.position ?: "",
                role = ""
            )
        )
    }

    fun getPlayers(teamId: String): Flow<List<Player>> = callbackFlow {
        val query = database.child("players").orderByChild("teamId").equalTo(teamId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Player::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getPlayer(playerId: String): Flow<Player?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Player::class.java))
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.child("players").child(playerId).addValueEventListener(listener)
        awaitClose { database.child("players").child(playerId).removeEventListener(listener) }
    }

    fun getAllPlayers(): Flow<List<Player>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Player::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.child("players").addValueEventListener(listener)
        awaitClose { database.child("players").removeEventListener(listener) }
    }

    suspend fun updatePlayerCareerPoints(playerId: String, pointsToAdd: Int) {
        val playerRef = database.child("players").child(playerId)
        val snapshot = playerRef.child("careerPoints").get().await()
        val currentPoints = snapshot.getValue(Int::class.java) ?: 0
        playerRef.child("careerPoints").setValue(currentPoints + pointsToAdd).await()
    }

    suspend fun incrementPlayerStat(playerId: String, statKey: String, increment: Int = 1) {
        val statRef = database.child("players").child(playerId).child("stats").child(statKey)
        val snapshot = statRef.get().await()
        val currentValue = snapshot.getValue(Int::class.java) ?: 0
        statRef.setValue(currentValue + increment).await()
        
        // Also update career points if it's a scoring stat
        if (statKey == "runs" || statKey == "goals" || statKey == "raid_points" || statKey == "points") {
            updatePlayerCareerPoints(playerId, increment)
        }
    }

    // Match & Scoring Methods
    suspend fun createMatch(match: Match) {
        database.child("matches").child(match.id).setValue(match).await()
        localDb.matchDao().insertMatch(
            MatchEntity(
                id = match.id,
                tournamentId = match.tournamentId,
                teamAId = match.teamAId,
                teamBId = match.teamBId,
                scoreA = 0,
                scoreB = 0,
                status = match.status.name,
                sportType = "KABADDI",
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateMatchScore(matchId: String, score: Score) {
        database.child("matches").child(matchId).child("score").setValue(score).await()
        val scoreA = when(score.type) {
            "KABADDI" -> score.teamAScore
            "VOLLEYBALL" -> score.teamASets
            "CRICKET" -> score.teamARuns
            "SOCCER" -> score.teamAGoals
            else -> 0
        }
        val scoreB = when(score.type) {
            "KABADDI" -> score.teamBScore
            "VOLLEYBALL" -> score.teamBSets
            "CRICKET" -> score.teamBRuns
            "SOCCER" -> score.teamBGoals
            else -> 0
        }
        localDb.matchDao().getMatchById(matchId)?.let {
            localDb.matchDao().insertMatch(it.copy(scoreA = scoreA, scoreB = scoreB, lastUpdated = System.currentTimeMillis()))
        }
    }

    suspend fun updateMatchStatus(matchId: String, status: MatchStatus) {
        database.child("matches").child(matchId).child("status").setValue(status.name).await()
        localDb.matchDao().getMatchById(matchId)?.let {
            localDb.matchDao().insertMatch(it.copy(status = status.name, lastUpdated = System.currentTimeMillis()))
        }
    }

    suspend fun updateManOfTheMatch(matchId: String, playerId: String) {
        val matchRef = database.child("matches").child(matchId)
        val oldMomSnapshot = matchRef.child("manOfTheMatchId").get().await()
        val oldMomId = oldMomSnapshot.getValue(String::class.java)

        if (oldMomId != playerId) {
            // Increment new MOM count
            val momCountRef = database.child("players").child(playerId).child("momCount")
            val currentCount = momCountRef.get().await().getValue(Int::class.java) ?: 0
            momCountRef.setValue(currentCount + 1).await()
            
            if (oldMomId != null) {
                val oldMomCountRef = database.child("players").child(oldMomId).child("momCount")
                val oldCount = oldMomCountRef.get().await().getValue(Int::class.java) ?: 0
                if (oldCount > 0) oldMomCountRef.setValue(oldCount - 1).await()
            }
        }

        matchRef.child("manOfTheMatchId").setValue(playerId).await()
    }

    suspend fun completeMatch(matchId: String, winnerId: String?) {
        database.child("matches").child(matchId).child("status").setValue(MatchStatus.COMPLETED.name).await()
        database.child("matches").child(matchId).child("winnerId").setValue(winnerId).await()
        database.child("matches").child(matchId).child("endTime").setValue(System.currentTimeMillis()).await()
        
        localDb.matchDao().getMatchById(matchId)?.let {
            localDb.matchDao().insertMatch(it.copy(status = MatchStatus.COMPLETED.name, lastUpdated = System.currentTimeMillis()))
        }
    }

    fun observeMatch(matchId: String): Flow<Match?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Match::class.java))
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.child("matches").child(matchId).addValueEventListener(listener)
        awaitClose { database.child("matches").child(matchId).removeEventListener(listener) }
    }

    fun observeLiveMatches(): Flow<List<Match>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Match::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.child("matches").addValueEventListener(listener)
        awaitClose { database.child("matches").removeEventListener(listener) }
    }

    fun observeMatchScore(matchId: String): Flow<Score?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val score = snapshot.getValue(Score::class.java)
                trySend(score)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.child("matches").child(matchId).child("score").addValueEventListener(listener)
        awaitClose { database.child("matches").child(matchId).child("score").removeEventListener(listener) }
    }
}
