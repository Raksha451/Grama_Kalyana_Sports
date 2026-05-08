package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramakalyana.sports.data.model.Player
import com.gramakalyana.sports.ui.viewmodel.PlayersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePlayersScreen(
    teamId: String,
    onBack: () -> Unit,
    onPlayerSelected: (String) -> Unit = {},
    showAppBar: Boolean = true,
    viewModel: PlayersViewModel = viewModel(),
    isAdmin: Boolean = false
) {
    LaunchedEffect(teamId) {
        viewModel.loadPlayers(teamId)
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var playerName by remember { mutableStateOf("") }
    var jerseyNumber by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }

    val players by viewModel.players.collectAsState()

    Scaffold(
        topBar = {
            if (showAppBar) {
                TopAppBar(
                    title = { Text("Manage Players", fontWeight = FontWeight.Bold) },
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
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { 
                        playerName = ""
                        jerseyNumber = ""
                        position = ""
                        showAddDialog = true 
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Player")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Player count display
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "Total Players in Team: ${players.size}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(players) { player ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        ListItem(
                            headlineContent = { Text(player.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                            supportingContent = { Text("Position: ${player.position} | Jersey: #${player.jerseyNumber}") },
                            leadingContent = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingContent = {
                                TextButton(
                                    onClick = { onPlayerSelected(player.id) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Profile →", fontWeight = FontWeight.Bold)
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text("Add New Player", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = playerName,
                            onValueChange = { playerName = it },
                            label = { Text("Player Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = jerseyNumber,
                                onValueChange = { jerseyNumber = it },
                                label = { Text("Jersey #") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = position,
                                onValueChange = { position = it },
                                label = { Text("Position") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            if (playerName.isNotBlank()) {
                                viewModel.addPlayer(
                                    Player(
                                        name = playerName,
                                        jerseyNumber = jerseyNumber.toIntOrNull(),
                                        position = position
                                    ),
                                    teamId
                                )
                                showAddDialog = false 
                            }
                        }
                    ) {
                        Text("SAVE PLAYER")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
