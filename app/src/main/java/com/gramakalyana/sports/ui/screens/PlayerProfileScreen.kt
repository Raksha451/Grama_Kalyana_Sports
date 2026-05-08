package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramakalyana.sports.data.model.Player
import com.gramakalyana.sports.data.model.SportType
import com.gramakalyana.sports.ui.viewmodel.PlayerProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    playerId: String, 
    onBack: () -> Unit,
    viewModel: PlayerProfileViewModel = viewModel()
) {
    val player by viewModel.player.collectAsState()
    val sportType by viewModel.sportType.collectAsState()

    LaunchedEffect(playerId) {
        viewModel.loadPlayerProfile(playerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player Profile", fontWeight = FontWeight.Bold) },
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player Header
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                player?.name ?: "Loading...", 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Black, 
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                "${player?.position ?: "Player"} | Jersey: #${player?.jerseyNumber ?: "-"}", 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            // Sport Type Badge
            if (sportType != null) {
                AssistChip(
                    onClick = { },
                    label = { Text(sportType?.name ?: "") },
                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.padding(top = 12.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = MaterialTheme.colorScheme.secondary,
                        leadingIconContentColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Highlights Card (Career Points & MOM)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Career Pts", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(player?.careerPoints?.toString() ?: "0", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text("MOM Awards", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(player?.momCount?.toString() ?: "0", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Primary Stats Grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (label, value) = when(sportType) {
                        SportType.CRICKET -> "Runs" to (player?.stats?.get("runs")?.toString() ?: "0")
                        SportType.KABADDI -> "Raid Pts" to (player?.stats?.get("raid_points")?.toString() ?: "0")
                        SportType.VOLLEYBALL -> "Attacks" to (player?.stats?.get("attacks")?.toString() ?: "0")
                        SportType.SOCCER -> "Goals" to (player?.stats?.get("goals")?.toString() ?: "0")
                        else -> "Matches" to (player?.stats?.get("matches")?.toString() ?: "0")
                    }
                    StatItem(label, value)
                    
                    VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    
                    val (label2, value2) = when(sportType) {
                        SportType.CRICKET -> "Wickets" to (player?.stats?.get("wickets")?.toString() ?: "0")
                        SportType.KABADDI -> "Tackle Pts" to (player?.stats?.get("tackle_points")?.toString() ?: "0")
                        SportType.VOLLEYBALL -> "Blocks" to (player?.stats?.get("blocks")?.toString() ?: "0")
                        SportType.SOCCER -> "Assists" to (player?.stats?.get("assists")?.toString() ?: "0")
                        else -> "Points" to (player?.stats?.get("points")?.toString() ?: "0")
                    }
                    StatItem(label2, value2)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Detailed Sport Performance
            Text(
                "Sport-Specific Metrics",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val stats = player?.stats ?: emptyMap()
                    if (stats.isEmpty() && sportType == null) {
                         Text("No detailed stats available yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        when(sportType) {
                            SportType.KABADDI -> {
                                MetricRow("Total Raids", stats["total_raids"]?.toString() ?: "0")
                                MetricRow("Success Raids", stats["success_raids"]?.toString() ?: "0")
                                MetricRow("Super Raids", stats["super_raids"]?.toString() ?: "0")
                                MetricRow("Super Tackles", stats["super_tackles"]?.toString() ?: "0")
                            }
                            SportType.CRICKET -> {
                                MetricRow("Matches Played", stats["matches"]?.toString() ?: "0")
                                MetricRow("Batting Average", stats["batting_avg"]?.toString() ?: "0.0")
                                MetricRow("Strike Rate", stats["strike_rate"]?.toString() ?: "0.0")
                                MetricRow("Best Bowling", stats["best_bowling"]?.toString() ?: "-")
                            }
                            SportType.VOLLEYBALL -> {
                                MetricRow("Service Aces", stats["aces"]?.toString() ?: "0")
                                MetricRow("Digs", stats["digs"]?.toString() ?: "0")
                                MetricRow("Success Rate", "${stats["success_rate"] ?: "0"}%")
                            }
                            SportType.SOCCER -> {
                                MetricRow("Yellow Cards", stats["yellow_cards"]?.toString() ?: "0")
                                MetricRow("Red Cards", stats["red_cards"]?.toString() ?: "0")
                                MetricRow("Minutes Played", stats["minutes"]?.toString() ?: "0")
                            }
                            else -> {
                                stats.forEach { (key, value) ->
                                    MetricRow(key.replaceFirstChar { it.uppercase() }, value.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 24.sp, color = MaterialTheme.colorScheme.secondary)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}
