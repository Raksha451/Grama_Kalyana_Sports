package com.gramakalyana.sports.data.model

import java.util.UUID

enum class SportType {
    KABADDI, VOLLEYBALL, CRICKET, SOCCER
}

enum class MatchStatus {
    SCHEDULED, LIVE, COMPLETED, CANCELLED
}

data class Tournament(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val sportType: SportType = SportType.KABADDI,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val venue: String = "",
    val organizerId: String = ""
)

data class Team(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val logoUrl: String? = null,
    val players: List<String> = emptyList(),
    val tournamentId: String = ""
)

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val teamId: String = "",
    val position: String? = null,
    val photoUrl: String? = null,
    val jerseyNumber: Int? = null,
    val stats: Map<String, Int> = emptyMap(),
    val careerPoints: Int = 0, // Historical career points tracking
    val momCount: Int = 0 // Man of the Match tracking
)

data class Match(
    val id: String = UUID.randomUUID().toString(),
    val tournamentId: String = "",
    val teamAId: String = "",
    val teamBId: String = "",
    val status: MatchStatus = MatchStatus.SCHEDULED,
    val startTime: Long = 0L,
    val endTime: Long? = null,
    val winnerId: String? = null,
    val manOfTheMatchId: String? = null,
    val score: Score? = null
)

data class Score(
    val type: String = "KABADDI",
    // Kabaddi fields
    val teamAScore: Int = 0,
    val teamBScore: Int = 0,
    val teamARaids: Int = 0,
    val teamBRaids: Int = 0,
    val teamATackles: Int = 0,
    val teamBTackles: Int = 0,
    // Volleyball fields
    val teamASets: Int = 0,
    val teamBSets: Int = 0,
    val currentSetAPoints: Int = 0,
    val currentSetBPoints: Int = 0,
    val setScores: List<String> = emptyList(),
    // Cricket fields
    val teamARuns: Int = 0,
    val teamAWickets: Int = 0,
    val teamAOvers: Float = 0.0f,
    val teamBRuns: Int = 0,
    val teamBWickets: Int = 0,
    val teamBOvers: Float = 0.0f,
    val currentInnings: Int = 1,
    // Soccer fields
    val teamAGoals: Int = 0,
    val teamBGoals: Int = 0
)
