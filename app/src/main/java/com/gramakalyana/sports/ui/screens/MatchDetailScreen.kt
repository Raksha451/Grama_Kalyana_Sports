package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramakalyana.sports.data.model.Score
import com.gramakalyana.sports.ui.viewmodel.MatchesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    onBack: () -> Unit,
    onViewScorecard: (String) -> Unit = {},
    viewModel: MatchesViewModel = viewModel()
) {
    val match by viewModel.currentMatch.collectAsState()

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Detail", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentMatch = match
            if (currentMatch == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentMatch.status.name, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val s = currentMatch.score ?: Score()
                        val scoreA = when (s.type) {
                            "KABADDI" -> s.teamAScore.toString()
                            "VOLLEYBALL" -> s.teamASets.toString()
                            "CRICKET" -> "${s.teamARuns}/${s.teamAWickets}"
                            else -> "0"
                        }
                        val scoreB = when (s.type) {
                            "KABADDI" -> s.teamBScore.toString()
                            "VOLLEYBALL" -> s.teamBSets.toString()
                            "CRICKET" -> "${s.teamBRuns}/${s.teamBWickets}"
                            else -> "0"
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TeamScoreInfo("Team A", scoreA)
                            Text("VS", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            TeamScoreInfo("Team B", scoreB)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("${currentMatch.status} | Group Match", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onViewScorecard(matchId) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Icon(Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VIEW FULL SCORECARD", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Match Statistics",
                    modifier = Modifier.align(Alignment.Start),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val s = currentMatch.score ?: Score()
                    when (s.type) {
                        "KABADDI" -> {
                            StatRow("Raids", s.teamARaids.toString(), s.teamBRaids.toString())
                            StatRow("Tackle Points", s.teamATackles.toString(), s.teamBTackles.toString())
                        }
                        "CRICKET" -> {
                            StatRow("Overs", s.teamAOvers.toString(), s.teamBOvers.toString())
                        }
                        else -> {
                            Text("No detailed stats available yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamScoreInfo(name: String, score: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(score, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black, fontSize = 36.sp)
    }
}

@Composable
fun StatRow(label: String, valA: String, valB: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(valA, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        Text(label, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
        Text(valB, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End, fontWeight = FontWeight.Bold)
    }
}
