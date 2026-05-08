package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.gramakalyana.sports.data.model.Score
import com.gramakalyana.sports.ui.utils.BitmapExporter
import com.gramakalyana.sports.ui.utils.ShareUtils
import com.gramakalyana.sports.ui.viewmodel.MatchesViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorecardPreviewScreen(
    matchId: String,
    onBack: () -> Unit,
    viewModel: MatchesViewModel = viewModel()
) {
    val match by viewModel.currentMatch.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Check if user is logged in (Admin/Scorer). Fans use public view without login.
    val isAuthorized = remember { FirebaseAuth.getInstance().currentUser != null }

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    val shareAction = {
        match?.let { currentMatch ->
            scope.launch {
                val bitmap = BitmapExporter.createScorecardBitmap(context, currentMatch)
                ShareUtils.shareBitmap(context, bitmap, "scorecard_${currentMatch.id}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scorecard Preview", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isAuthorized) {
                        IconButton(onClick = { shareAction() }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "GRAMA KALYANA SPORTS",
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                        Text("Official Match Scorecard", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        val s = currentMatch.score ?: Score()
                        val (scoreA, scoreB) = when (s.type) {
                            "KABADDI" -> s.teamAScore.toString() to s.teamBScore.toString()
                            "VOLLEYBALL" -> s.teamASets.toString() to s.teamBSets.toString()
                            "CRICKET" -> "${s.teamARuns}/${s.teamAWickets}" to "${s.teamBRuns}/${s.teamBWickets}"
                            "SOCCER" -> s.teamAGoals.toString() to s.teamBGoals.toString()
                            else -> "0" to "0"
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScorecardTeamItem("TEAM A", scoreA)
                            Text("VS", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                            ScorecardTeamItem("TEAM B", scoreB)
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        DashedDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        Text("Status: ${currentMatch.status.name}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Match ID: ${currentMatch.id.take(8)}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Date: ${sdf.format(Date(currentMatch.startTime))}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        
                        if (currentMatch.manOfTheMatchId != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Man of the Match Selected", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val winnerText = if (currentMatch.status.name == "COMPLETED") {
                             val sA = when(s.type) {
                                 "CRICKET" -> s.teamARuns
                                 "SOCCER" -> s.teamAGoals
                                 "VOLLEYBALL" -> s.teamASets
                                 else -> s.teamAScore
                             }
                             val sB = when(s.type) {
                                 "CRICKET" -> s.teamBRuns
                                 "SOCCER" -> s.teamBGoals
                                 "VOLLEYBALL" -> s.teamBSets
                                 else -> s.teamBScore
                             }
                             if (sA > sB) "WINNER: TEAM A" else if (sB > sA) "WINNER: TEAM B" else "RESULT: DRAW"
                        } else {
                            "MATCH IN PROGRESS"
                        }
                        
                        Text(winnerText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text("Powered by Grama Kalyana App", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                if (isAuthorized) {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = { shareAction() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EXPORT & SHARE SCORECARD", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ScorecardTeamItem(name: String, score: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(score, fontWeight = FontWeight.Black, fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun DashedDivider(color: Color) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}
