package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramakalyana.sports.data.model.*
import com.gramakalyana.sports.ui.viewmodel.ScoringViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScoringScreen(
    matchId: String,
    onBack: () -> Unit,
    onFinish: (String) -> Unit,
    viewModel: ScoringViewModel = viewModel()
) {
    val matchState by viewModel.match.collectAsState()
    val players by viewModel.players.collectAsState()
    val teamA by viewModel.teamA.collectAsState()
    val teamB by viewModel.teamB.collectAsState()
    val tournament by viewModel.tournament.collectAsState()

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Scoring", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        val currentMatch = matchState
        if (currentMatch == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ScoreboardHeader(currentMatch, teamA?.name ?: "Team A", teamB?.name ?: "Team B")
                }

                if (currentMatch.status == MatchStatus.SCHEDULED) {
                    item {
                        Button(
                            onClick = { 
                                val initialScore = Score(type = tournament?.sportType?.name ?: "KABADDI")
                                viewModel.updateScore(matchId, initialScore)
                                // Assuming repository has a way to update status, or we do it via updateScore if it handles it
                                // For now, let's just make sure the score object exists with correct type
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("START MATCH", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    val sportType = tournament?.sportType?.name ?: currentMatch.score?.type ?: "KABADDI"
                    val currentScore = currentMatch.score ?: Score(type = sportType)
                    
                    when (sportType) {
                        "KABADDI" -> KabaddiControls(currentScore, teamA?.name ?: "A", teamB?.name ?: "B") { viewModel.updateScore(matchId, it) }
                        "VOLLEYBALL" -> VolleyballControls(currentScore, teamA?.name ?: "A", teamB?.name ?: "B") { viewModel.updateScore(matchId, it) }
                        "CRICKET" -> CricketControls(currentScore, teamA?.name ?: "A", teamB?.name ?: "B") { viewModel.updateScore(matchId, it) }
                        "SOCCER" -> SoccerControls(currentScore, teamA?.name ?: "A", teamB?.name ?: "B") { viewModel.updateScore(matchId, it) }
                    }
                }

                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        "Update Player Stats", 
                        fontWeight = FontWeight.Bold, 
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(players.filter { it.teamId == currentMatch.teamAId || it.teamId == currentMatch.teamBId }) { player ->
                    PlayerScoringRow(
                        player = player,
                        isMOM = currentMatch.manOfTheMatchId == player.id,
                        sportType = tournament?.sportType?.name ?: currentMatch.score?.type ?: "KABADDI",
                        onSelectMOM = { viewModel.updateManOfTheMatch(matchId, player.id) },
                        onUpdateStat = { statKey, increment -> viewModel.updatePlayerStat(player.id, statKey, increment) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { onFinish(matchId) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("FINISH MATCH", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreboardHeader(match: Match, nameA: String, nameB: String) {
    val score = match.score ?: Score()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (scoreA, scoreB) = when (score.type) {
                "KABADDI" -> score.teamAScore.toString() to score.teamBScore.toString()
                "VOLLEYBALL" -> score.teamASets.toString() to score.teamBSets.toString()
                "CRICKET" -> "${score.teamARuns}/${score.teamAWickets}" to "${score.teamBRuns}/${score.teamBWickets}"
                "SOCCER" -> score.teamAGoals.toString() to score.teamBGoals.toString()
                else -> "0" to "0"
            }
            TeamScoreBox(nameA, scoreA)
            Text("VS", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            TeamScoreBox(nameB, scoreB)
        }
    }
}

@Composable
fun PlayerScoringRow(
    player: Player,
    isMOM: Boolean,
    sportType: String,
    onSelectMOM: () -> Unit,
    onUpdateStat: (String, Int) -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(player.name, color = MaterialTheme.colorScheme.onSurface) },
        supportingContent = { Text("Pts: ${player.careerPoints} | MOMs: ${player.momCount}", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val buttonColors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                when (sportType) {
                    "CRICKET" -> {
                        TextButton(onClick = { onUpdateStat("runs", 1) }, colors = buttonColors) { Text("+1R") }
                        TextButton(onClick = { onUpdateStat("wickets", 1) }, colors = buttonColors) { Text("+1W") }
                    }
                    "SOCCER" -> {
                        TextButton(onClick = { onUpdateStat("goals", 1) }, colors = buttonColors) { Text("+1G") }
                    }
                    "KABADDI" -> {
                        TextButton(onClick = { onUpdateStat("raid_points", 1) }, colors = buttonColors) { Text("+1RP") }
                        TextButton(onClick = { onUpdateStat("tackle_points", 1) }, colors = buttonColors) { Text("+1TP") }
                    }
                    else -> {
                        TextButton(onClick = { onUpdateStat("points", 1) }, colors = buttonColors) { Text("+1 Pts") }
                    }
                }
                IconButton(onClick = onSelectMOM) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "MOM",
                        tint = if (isMOM) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }
    )
}

@Composable
fun SoccerControls(score: Score, nameA: String, nameB: String, onUpdate: (Score) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Soccer Goals", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { onUpdate(score.copy(type = "SOCCER", teamAGoals = score.teamAGoals + 1)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Goal $nameA") }
            Button(
                onClick = { onUpdate(score.copy(type = "SOCCER", teamBGoals = score.teamBGoals + 1)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Goal $nameB") }
        }
    }
}

@Composable
fun KabaddiControls(score: Score, nameA: String, nameB: String, onUpdate: (Score) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { onUpdate(score.copy(teamAScore = score.teamAScore + 1)) }) { Text("+1 $nameA") }
            Button(onClick = { onUpdate(score.copy(teamBScore = score.teamBScore + 1)) }) { Text("+1 $nameB") }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { onUpdate(score.copy(teamARaids = score.teamARaids + 1)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Raid $nameA") }
            Button(
                onClick = { onUpdate(score.copy(teamBRaids = score.teamBRaids + 1)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Raid $nameB") }
        }
    }
}

@Composable
fun VolleyballControls(score: Score, nameA: String, nameB: String, onUpdate: (Score) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = { onUpdate(score.copy(currentSetAPoints = score.currentSetAPoints + 1)) }) { Text("Point $nameA") }
        Button(onClick = { onUpdate(score.copy(currentSetBPoints = score.currentSetBPoints + 1)) }) { Text("Point $nameB") }
    }
}

@Composable
fun CricketControls(score: Score, nameA: String, nameB: String, onUpdate: (Score) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Innings: ${score.currentInnings}", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onUpdate(score.copy(teamARuns = score.teamARuns + 1)) }) { Text("1 Run") }
            Button(onClick = { onUpdate(score.copy(teamARuns = score.teamARuns + 4)) }) { Text("4 Runs") }
            Button(onClick = { onUpdate(score.copy(teamARuns = score.teamARuns + 6)) }) { Text("6 Runs") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onUpdate(score.copy(teamAWickets = score.teamAWickets + 1)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("WKT $nameA") }
            Button(
                onClick = { onUpdate(score.copy(teamBWickets = score.teamBWickets + 1)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("WKT $nameB") }
        }
    }
}

@Composable
fun TeamScoreBox(name: String, score: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(score, fontSize = 32.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
    }
}
