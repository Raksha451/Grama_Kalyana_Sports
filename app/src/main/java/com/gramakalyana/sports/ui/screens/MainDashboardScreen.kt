package com.gramakalyana.sports.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gramakalyana.sports.data.model.Match
import com.gramakalyana.sports.ui.navigation.Screen
import com.gramakalyana.sports.ui.viewmodel.MatchesViewModel

@Composable
fun MainDashboardScreen(navController: NavHostController, userRole: String = "scorer") {
    val bottomNavController = rememberNavController()
    
    val items = listOf(
        NavigationItem("Home", Icons.Default.Home, "home_tab"),
        NavigationItem("Tournaments", Icons.Default.EmojiEvents, "tournaments_tab"),
        NavigationItem("Players", Icons.Default.Person, "players_tab"),
        NavigationItem("Profile", Icons.Default.Sports, "profile_tab")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                bottomNavController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) { saveState = true }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home_tab",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home_tab") {
                LiveMatchesScorerView(
                    onMatchSelected = { matchId ->
                        navController.navigate(Screen.LiveScoring.createRoute(matchId, userRole))
                    }
                )
            }
            composable("tournaments_tab") {
                TournamentListScreen(
                    onTournamentSelected = { id ->
                        navController.navigate(Screen.TournamentDetail.createRoute(id, userRole))
                    },
                    onCreateTournament = {
                        navController.navigate(Screen.CreateTournament.route)
                    },
                    onLogout = {
                        navController.navigate(Screen.RoleSelection.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    },
                    isAdmin = userRole == "admin"
                )
            }
            composable("players_tab") {
                ManagePlayersScreen(
                    teamId = "global", 
                    onBack = { bottomNavController.navigate("home_tab") },
                    onPlayerSelected = { id ->
                        navController.navigate(Screen.PlayerProfile.createRoute(id))
                    },
                    isAdmin = userRole == "admin"
                )
            }
            composable("profile_tab") {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.RoleSelection.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    },
                    userRole = userRole
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMatchesScorerView(
    onMatchSelected: (String) -> Unit,
    viewModel: MatchesViewModel = viewModel()
) {
    val matches by viewModel.matches.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllMatches()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Matches to Score",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (matches.isEmpty()) {
            Text("No assigned matches to score.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(matches) { match ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onMatchSelected(match.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Match ID: ${match.id.take(8)}", fontWeight = FontWeight.Bold)
                            Text("${match.status} | Start Time: ${match.startTime}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { onMatchSelected(match.id) }) {
                                Text("CONTINUE SCORING")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class NavigationItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)
