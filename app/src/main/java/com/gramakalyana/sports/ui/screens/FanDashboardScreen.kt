package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramakalyana.sports.data.model.Match
import com.gramakalyana.sports.data.model.Player
import com.gramakalyana.sports.data.model.Score
import com.gramakalyana.sports.data.model.Tournament
import com.gramakalyana.sports.ui.viewmodel.FanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FanDashboardScreen(
    onAdminLogin: () -> Unit,
    onMatchSelected: (String) -> Unit,
    onTournamentSelected: (String) -> Unit,
    viewModel: FanViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("LIVE", "TOURNAMENTS", "PLAYERS")

    val liveMatches by viewModel.liveMatches.collectAsState()
    val tournaments by viewModel.tournaments.collectAsState()
    val players by viewModel.players.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grama Kalyana", fontWeight = FontWeight.Black) },
                actions = {
                    Button(
                        onClick = onAdminLogin,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text("Scorer Login", fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                color = if (selectedTab == index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary
                            ) 
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> LiveMatchesTab(liveMatches, onMatchSelected)
                1 -> PublicTournamentsTab(tournaments, onTournamentSelected)
                2 -> PublicPlayersTab(players)
            }
        }
    }
}

@Composable
fun LiveMatchesTab(matches: List<Match>, onMatchSelected: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Text(
                    "Live Now",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
        
        if (matches.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No live matches at the moment", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(matches) { match ->
                val s = match.score ?: Score()
                val scoreText = when (s.type) {
                    "KABADDI" -> "${s.teamAScore} - ${s.teamBScore}"
                    "VOLLEYBALL" -> "${s.teamASets} - ${s.teamBSets}"
                    "CRICKET" -> "${s.teamARuns}/${s.teamAWickets} - ${s.teamBRuns}/${s.teamBWickets}"
                    "SOCCER" -> "${s.teamAGoals} - ${s.teamBGoals}"
                    else -> "0 - 0"
                }
                LiveMatchCard("Team A", "Team B", scoreText, s.type) { onMatchSelected(match.id) }
            }
        }
    }
}

@Composable
fun PublicTournamentsTab(tournaments: List<Tournament>, onTournamentSelected: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (tournaments.isEmpty()) {
            item {
                Text("No active tournaments found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(tournaments) { tournament ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onTournamentSelected(tournament.id) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(tournament.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Location: ${tournament.venue}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Sport: ${tournament.sportType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
fun PublicPlayersTab(players: List<Player>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (players.isEmpty()) {
            item {
                Text("No players registered yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(players) { player ->
                ListItem(
                    headlineContent = { Text(player.name, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("${player.position ?: "Player"} | ${player.careerPoints} Career Points") },
                    leadingContent = { 
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        ) 
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMatchCard(teamA: String, teamB: String, score: String, sport: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(sport, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(teamA, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(score, fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                Text(teamB, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            Text("Updating live...", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
