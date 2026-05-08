package com.gramakalyana.sports.data.local

import androidx.room.*

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sportType: String,
    val venue: String,
    val startDate: Long,
    val organizerId: String
)

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val tournamentId: String,
    val teamAId: String,
    val teamBId: String,
    val scoreA: Int,
    val scoreB: Int,
    val status: String,
    val sportType: String,
    val lastUpdated: Long
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val villageName: String,
    val tournamentId: String
)

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val teamId: String,
    val jerseyNumber: String,
    val position: String,
    val role: String
)

@Dao
interface TournamentDao {
    @Query("SELECT * FROM tournaments")
    suspend fun getAllTournaments(): List<TournamentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity)
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches")
    suspend fun getAllMatches(): List<MatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): MatchEntity?
}

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams WHERE tournamentId = :tournamentId")
    suspend fun getTeamsForTournament(tournamentId: String): List<TeamEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)
}

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE teamId = :teamId")
    suspend fun getPlayersForTeam(teamId: String): List<PlayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)
}

@Database(entities = [TournamentEntity::class, MatchEntity::class, TeamEntity::class, PlayerEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
    abstract fun matchDao(): MatchDao
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
}
