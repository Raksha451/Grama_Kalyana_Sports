package com.gramakalyana.sports.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gramakalyana.sports.ui.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object RoleSelection : Screen("role_selection")
    object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    object MainDashboard : Screen("main_dashboard/{role}") {
        fun createRoute(role: String) = "main_dashboard/$role"
    }
    object TournamentList : Screen("tournament_list")
    object CreateTournament : Screen("create_tournament")
    object TournamentDetail : Screen("tournament_detail/{tournamentId}/{role}") {
        fun createRoute(tournamentId: String, role: String) = "tournament_detail/$tournamentId/$role"
    }
    object LiveScoring : Screen("live_scoring/{matchId}/{role}") {
        fun createRoute(matchId: String, role: String) = "live_scoring/$matchId/$role"
    }
    object FanDashboard : Screen("fan_dashboard")
    object MatchDetail : Screen("match_detail/{matchId}") {
        fun createRoute(matchId: String) = "match_detail/$matchId"
    }
    object MatchResult : Screen("match_result/{matchId}") {
        fun createRoute(matchId: String) = "match_result/$matchId"
    }
    object ManageTeams : Screen("manage_teams/{tournamentId}/{role}") {
        fun createRoute(tournamentId: String, role: String) = "manage_teams/$tournamentId/$role"
    }
    object ManagePlayers : Screen("manage_players/{teamId}/{role}") {
        fun createRoute(teamId: String, role: String) = "manage_players/$teamId/$role"
    }
    object MatchSchedule : Screen("match_schedule/{tournamentId}/{role}") {
        fun createRoute(tournamentId: String, role: String) = "match_schedule/$tournamentId/$role"
    }
    object CreateMatch : Screen("create_match/{tournamentId}") {
        fun createRoute(tournamentId: String) = "create_match/$tournamentId"
    }
    object PlayerProfile : Screen("player_profile/{playerId}") {
        fun createRoute(playerId: String) = "player_profile/$playerId"
    }
    object ScorecardPreview : Screen("scorecard_preview/{matchId}") {
        fun createRoute(matchId: String) = "scorecard_preview/$matchId"
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNext = {
                navController.navigate(Screen.RoleSelection.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onAdminSelected = { navController.navigate(Screen.Login.createRoute("admin")) },
                onScorerSelected = { navController.navigate(Screen.Login.createRoute("scorer")) },
                onFanSelected = { 
                    navController.navigate(Screen.FanDashboard.route) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Login.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "scorer"
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.MainDashboard.createRoute(role)) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.MainDashboard.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "scorer"
            MainDashboardScreen(navController = navController, userRole = role)
        }
        composable(Screen.TournamentList.route) {
            TournamentListScreen(
                onTournamentSelected = { tournamentId ->
                    navController.navigate(Screen.TournamentDetail.createRoute(tournamentId, "fan"))
                },
                onCreateTournament = {
                    navController.navigate(Screen.CreateTournament.route)
                },
                onLogout = {
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.CreateTournament.route) {
            CreateTournamentScreen(
                onBack = { navController.popBackStack() },
                onTournamentCreated = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.TournamentDetail.route,
            arguments = listOf(
                navArgument("tournamentId") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "fan"
            TournamentDetailScreen(
                tournamentId = tournamentId,
                onBack = { navController.popBackStack() },
                onManageTeams = { navController.navigate(Screen.ManageTeams.createRoute(tournamentId, role)) },
                onTeamSelected = { teamId ->
                    navController.navigate(Screen.ManagePlayers.createRoute(teamId, role))
                },
                onViewSchedule = { navController.navigate(Screen.MatchSchedule.createRoute(tournamentId, role)) },
                isAdmin = role == "admin"
            )
        }
        composable(
            route = Screen.LiveScoring.route,
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "scorer"
            LiveScoringScreen(
                matchId = matchId,
                onBack = { navController.popBackStack() },
                onFinish = { id ->
                    navController.navigate(Screen.MatchResult.createRoute(id)) {
                        popUpTo(Screen.LiveScoring.createRoute(matchId, role)) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.FanDashboard.route) {
            FanDashboardScreen(
                onAdminLogin = { navController.navigate(Screen.RoleSelection.route) },
                onMatchSelected = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId))
                },
                onTournamentSelected = { tournamentId ->
                    navController.navigate(Screen.TournamentDetail.createRoute(tournamentId, "fan"))
                }
            )
        }
        composable(Screen.MatchDetail.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchDetailScreen(
                matchId = matchId,
                onBack = { navController.popBackStack() },
                onViewScorecard = { id ->
                    navController.navigate(Screen.ScorecardPreview.createRoute(id))
                }
            )
        }
        composable(Screen.MatchResult.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchResultScreen(
                matchId = matchId,
                onBack = { navController.popBackStack() },
                onShare = {
                    navController.navigate(Screen.ScorecardPreview.createRoute(matchId))
                }
            )
        }
        composable(
            route = Screen.ManageTeams.route,
            arguments = listOf(
                navArgument("tournamentId") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "fan"
            ManageTeamsScreen(
                tournamentId = tournamentId,
                onBack = { navController.popBackStack() },
                isAdmin = role == "admin"
            )
        }
        composable(
            route = Screen.ManagePlayers.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "fan"
            ManagePlayersScreen(
                teamId = teamId,
                onBack = { navController.popBackStack() },
                onPlayerSelected = { playerId ->
                    navController.navigate(Screen.PlayerProfile.createRoute(playerId))
                },
                isAdmin = role == "admin"
            )
        }
        composable(
            route = Screen.MatchSchedule.route,
            arguments = listOf(
                navArgument("tournamentId") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "fan"
            MatchScheduleScreen(
                tournamentId = tournamentId,
                onBack = { navController.popBackStack() },
                onScheduleMatch = { navController.navigate(Screen.CreateMatch.createRoute(tournamentId)) },
                isAdmin = role == "admin"
            )
        }
        composable(Screen.CreateMatch.route) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""
            CreateMatchScreen(
                tournamentId = tournamentId,
                onBack = { navController.popBackStack() },
                onMatchCreated = { navController.popBackStack() }
            )
        }
        composable(Screen.PlayerProfile.route) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
            PlayerProfileScreen(
                playerId = playerId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ScorecardPreview.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            ScorecardPreviewScreen(
                matchId = matchId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
