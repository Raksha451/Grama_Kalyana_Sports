package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramakalyana.sports.data.model.Tournament
import com.gramakalyana.sports.data.model.Team
import com.gramakalyana.sports.ui.viewmodel.TeamsViewModel
import com.gramakalyana.sports.ui.viewmodel.TournamentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    onBack: () -> Unit,
    onManageTeams: () -> Unit,
    onTeamSelected: (String) -> Unit,
    onViewSchedule: () -> Unit,
    tournamentViewModel: TournamentViewModel = viewModel(),
    teamsViewModel: TeamsViewModel = viewModel(),
    isAdmin: Boolean = false
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Teams", "Schedule")
    
    val tournaments by tournamentViewModel.tournaments.collectAsState()
    val tournament = tournaments.find { it.id == tournamentId }
    
    val teams by teamsViewModel.teams.collectAsState()

    LaunchedEffect(tournamentId) {
        teamsViewModel.loadTeams(tournamentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tournament?.name ?: "Tournament Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                0 -> TournamentOverviewContent(tournament)
                1 -> TournamentTeamsContent(teams, onManageTeams, onTeamSelected, isAdmin)
                2 -> TournamentScheduleContent(onViewSchedule)
            }
        }
    }
}

@Composable
fun TournamentOverviewContent(tournament: Tournament?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(tournament?.name ?: "Loading...", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Location: ${tournament?.venue ?: "N/A"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Sport: ${tournament?.sportType?.name ?: "N/A"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Stats Summary", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total Teams: --", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Matches Played: --", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun TournamentTeamsContent(
    teams: List<Team>, 
    onManageTeams: () -> Unit,
    onTeamSelected: (String) -> Unit,
    isAdmin: Boolean
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Participating Teams", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            if (isAdmin) {
                TextButton(
                    onClick = onManageTeams,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Add/Edit Teams", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        LazyColumn {
            items(teams) { team ->
                ListItem(
                    modifier = Modifier.clickable { onTeamSelected(team.id) },
                    headlineContent = { Text(team.name, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("${team.players.size} Players") },
                    trailingContent = { Text("View Players →", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
fun TournamentScheduleContent(onViewSchedule: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = onViewSchedule, 
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("FULL SCHEDULE / FIXTURES", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Quick View", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Navigate to full schedule to see all matches.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
