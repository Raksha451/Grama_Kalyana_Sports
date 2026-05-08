package com.gramakalyana.sports.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramakalyana.sports.data.model.Match
import com.gramakalyana.sports.data.model.MatchStatus
import com.gramakalyana.sports.ui.viewmodel.MatchesViewModel
import com.gramakalyana.sports.ui.viewmodel.TeamsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatchScreen(
    tournamentId: String,
    onBack: () -> Unit,
    onMatchCreated: () -> Unit,
    matchesViewModel: MatchesViewModel = viewModel(),
    teamsViewModel: TeamsViewModel = viewModel()
) {
    val teams by teamsViewModel.teams.collectAsState()
    var teamAId by remember { mutableStateOf("") }
    var teamBId by remember { mutableStateOf("") }
    var matchDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    LaunchedEffect(tournamentId) {
        teamsViewModel.loadTeams(tournamentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Match", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Select Teams", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            // Team A Selection
            Text("Team A")
            TeamDropdown(teams, teamAId) { teamAId = it }

            // Team B Selection
            Text("Team B")
            TeamDropdown(teams, teamBId) { teamBId = it }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Date & Time", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            
            val sdf = SimpleDateFormat("EEE, MMM dd, yyyy - hh:mm a", Locale.getDefault())
            OutlinedButton(
                onClick = {
                    DatePickerDialog(context, { _, y, m, d ->
                        calendar.set(y, m, d)
                        TimePickerDialog(context, { _, hh, mm ->
                            calendar.set(Calendar.HOUR_OF_DAY, hh)
                            calendar.set(Calendar.MINUTE, mm)
                            matchDate = calendar.timeInMillis
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(sdf.format(Date(matchDate)))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (teamAId.isNotEmpty() && teamBId.isNotEmpty() && teamAId != teamBId) {
                        val newMatch = Match(
                            tournamentId = tournamentId,
                            teamAId = teamAId,
                            teamBId = teamBId,
                            startTime = matchDate,
                            status = MatchStatus.SCHEDULED
                        )
                        matchesViewModel.createMatch(newMatch)
                        onMatchCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = teamAId.isNotEmpty() && teamBId.isNotEmpty() && teamAId != teamBId
            ) {
                Text("SCHEDULE MATCH", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDropdown(teams: List<com.gramakalyana.sports.data.model.Team>, selectedId: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTeam = teams.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedTeam?.name ?: "Select Team",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            teams.forEach { team ->
                DropdownMenuItem(
                    text = { Text(team.name) },
                    onClick = {
                        onSelected(team.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
