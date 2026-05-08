package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.gramakalyana.sports.data.model.Score
import com.gramakalyana.sports.ui.viewmodel.MatchesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchResultScreen(
    matchId: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    viewModel: MatchesViewModel = viewModel()
) {
    val match by viewModel.currentMatch.collectAsState()
    
    // Check if user is logged in (Admin/Scorer). Fans use public view without login.
    val isAuthorized = remember { FirebaseAuth.getInstance().currentUser != null }

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Result", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isAuthorized) {
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onPrimary)
                        }
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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("FINAL RESULT", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(16.dp))

                        val s = currentMatch.score ?: Score()
                        val (scoreA, scoreB) = when (s.type) {
                            "KABADDI" -> s.teamAScore.toString() to s.teamBScore.toString()
                            "VOLLEYBALL" -> s.teamASets.toString() to s.teamBSets.toString()
                            "CRICKET" -> s.teamARuns.toString() to s.teamBRuns.toString()
                            else -> "0" to "0"
                        }

                        val valA = scoreA.split("/")[0].toIntOrNull() ?: 0
                        val valB = scoreB.split("/")[0].toIntOrNull() ?: 0
                        val winA = valA > valB
                        val winB = valB > valA

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ResultTeamInfo("Team A", scoreA, isWinner = winA)
                            Text("VS", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            ResultTeamInfo("Team B", scoreB, isWinner = winB)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        val resultText = if (winA) "Team A won the match" else if (winB) "Team B won the match" else "Match Tied"
                        Text(resultText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Man of the Match", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(25.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Pending Selection", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Stats will be updated soon", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (isAuthorized) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onShare,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SHARE FULL SCORECARD", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ResultTeamInfo(name: String, score: String, isWinner: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontWeight = if (isWinner) FontWeight.Black else FontWeight.Normal)
        Text(score, color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Black, fontSize = 40.sp)
        if (isWinner) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "WINNER", 
                    color = MaterialTheme.colorScheme.onPrimary, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
