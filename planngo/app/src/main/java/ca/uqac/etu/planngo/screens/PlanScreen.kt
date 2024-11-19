package ca.uqac.etu.planngo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.viewmodel.ActivityViewModel

@Composable
fun PlanScreen() {
    var date by remember { mutableStateOf(TextFieldValue()) }
    var startTime by remember { mutableStateOf(TextFieldValue()) }
    var duration by remember { mutableIntStateOf(2) } // duration is an integer
    var showModal by remember { mutableStateOf(false) }
    var plannedActivities by remember { mutableStateOf<List<String>>(emptyList()) }

    val activityViewModel = ActivityViewModel()
    val activities = activityViewModel.getActivities()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PromotionBanner()
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (DD-MM-YYYY)") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Heure de début (HH:mm)") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = duration.toString(),
                onValueChange = { newValue ->
                    val newDuration = newValue.toIntOrNull()
                    if (newDuration != null) {
                        duration = newDuration
                    }
                },
                label = { Text("Durée (en heures)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Logique pour planifier les activités
                plannedActivities = planActivities(activities, duration)
                showModal = true
            }) {
                Text("Planifier")
            }
        }
    }

    if (showModal) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            confirmButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Fermer")
                }
            },
            title = { Text("Suggestions pour la journée du ${date.text} à partir de ${startTime.text}") },
            text = {
                Column {
                    for (activity in plannedActivities) {
                        Text(activity)
                    }
                }
            }
        )
    }
}

fun planActivities(activities: List<Activity>, duration: Int): List<String> {
    val plannedActivities = mutableListOf<String>()

    // Utiliser la durée sous forme d'entier directement
    for (activity in activities) {
        if (activity.duration <= duration) {
            val activityInfo = "${activity.name} - ${activity.hours["start"]} - ${activity.hours["end"]}"
            plannedActivities.add(activityInfo)
        }
    }

    return plannedActivities
}
