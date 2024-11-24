package ca.uqac.etu.planngo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.viewmodel.ActivityViewModel
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import ca.uqac.etu.planngo.data.LocalStorage
import ca.uqac.etu.planngo.models.DayPlan
import java.util.Calendar
import kotlinx.coroutines.launch


@Composable
fun PlanScreen() {
    // Variables d'état pour gérer la date, l'heure, la durée, etc.
    var date by remember { mutableStateOf(String()) }
    var startTime by remember { mutableStateOf(String()) }
    var duration by remember { mutableIntStateOf(2) } // duration is an integer
    var showModal by remember { mutableStateOf(false) }
    var plannedActivities by remember { mutableStateOf<List<String>>(emptyList()) }
    var isDateError by remember { mutableStateOf(false) }
    var isTimeError by remember { mutableStateOf(false) }
    var isDurationError by remember { mutableStateOf(false) }

    // Initialisation du calendrier et du contexte
    val calendar = Calendar.getInstance()
    val context = LocalContext.current
    val activityViewModel = ActivityViewModel()
    val activities = activityViewModel.getActivities()

    // Coroutine scope pour gérer les appels asynchrones
    val scope = rememberCoroutineScope()

    // Conteneur principal de la vue
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titre de la page
            Text(
                text = "Plannification de journées 🤖",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp, start = 10.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Sélection de la date via un bouton déclenchant le DatePickerDialog
            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val formattedDate = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
                        date = formattedDate
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (date.isEmpty()) "Sélectionner une date" else "Date : $date")
            }
            if (isDateError) {
                Text("Veuillez sélectionner une date.", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sélection de l'heure via un bouton déclenchant le TimePickerDialog
            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                        startTime = formattedTime
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true // 24-hour format
                ).show()
            }) {
                Text(if (startTime.isEmpty()) "Sélectionner une heure" else "Heure : $startTime")
            }
            if (isTimeError) {
                Text("Veuillez sélectionner une heure.", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Affichage de la durée et du Slider pour la modifier
            Text(
                text = "Durée (en heures) : $duration",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = duration.toFloat(),
                onValueChange = { newValue ->
                    duration = newValue.toInt()
                },
                valueRange = 1f..12f,
                steps = 11,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (isDurationError) {
                Text("Veuillez sélectionner une durée valide.", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bouton pour valider la planification de l'activité
            Button(onClick = {
                // Vérification des erreurs de date, heure et durée
                isDateError = date.isEmpty()
                isTimeError = startTime.isEmpty()
                isDurationError = duration <= 0

                if (!isDateError && !isTimeError && !isDurationError) {
                    // Planification des activités
                    plannedActivities = planActivities(activities, duration, startTime)
                    showModal = true

                    // Sauvegarde des activités planifiées
                    val newDayPlan = DayPlan(
                        date = date,
                        startTime = startTime,
                        activities = plannedActivities
                    )
                    scope.launch {
                        LocalStorage.addPlannedDay(context, newDayPlan)
                    }
                }
            }) {
                Text("Découvrir ...")
            }
        }
    }

    // Modal affichant les activités planifiées
    if (showModal) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            confirmButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Fermer")
                }
            },
            title = { Text("Suggestions pour la journée du ${date} à partir de ${startTime}") },
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

// Fonction de planification des activités en fonction de la durée et de l'heure de départ
fun planActivities(activities: List<Activity>, duration: Int, startTime: String): List<String> {
    val plannedActivities = mutableListOf<String>()

    // Convertir startTime en minutes pour comparaison
    val (startHour, startMinute) = startTime.split(":").map { it.toInt() }
    val startInMinutes = startHour * 60 + startMinute

    // Parcours des activités pour vérifier si elles peuvent être planifiées
    for (activity in activities) {
        // Convertir les horaires de l'activité en minutes
        val activityStart = activity.hours["start"]?.split(":")?.map { it.toInt() }
        val activityEnd = activity.hours["end"]?.split(":")?.map { it.toInt() }

        if (activityStart != null && activityEnd != null) {
            val activityStartInMinutes = activityStart[0] * 60 + activityStart[1]
            val activityEndInMinutes = activityEnd[0] * 60 + activityEnd[1]

            // Vérifier si l'activité correspond à la durée disponible et aux horaires
            if (
                activity.duration <= duration &&
                startInMinutes >= activityStartInMinutes &&
                startInMinutes + (duration * 60) <= activityEndInMinutes
            ) {
                val activityInfo = "${activity.name} - ${activity.hours["start"]} - ${activity.hours["end"]}"
                plannedActivities.add(activityInfo)
            }
        }
    }

    return plannedActivities
}


