package ca.uqac.etu.planngo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.uqac.etu.planngo.models.DayPlan
import ca.uqac.etu.planngo.data.LocalStorage
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val plannedDays = remember { mutableStateListOf<DayPlan>() }
    var showDetailsModal by remember { mutableStateOf(false) }
    var showDeleteModal by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<DayPlan?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Charger les journées planifiées depuis le DataStore
    LaunchedEffect(Unit) {
        LocalStorage.getPlannedDaysFlow(context).collect { days ->
            plannedDays.clear()
            plannedDays.addAll(days)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (plannedDays.isEmpty()) {
            Text(
                text = "Aucune journée planifiée pour l'instant.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                plannedDays.forEach { dayPlan ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            selectedDay = dayPlan
                                            showDeleteModal = true
                                        },
                                        onTap = {
                                            selectedDay = dayPlan
                                            showDetailsModal = true
                                        }
                                    )
                                },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Le ${dayPlan.date}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "À partir de ${dayPlan.startTime}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Affichage de la modale pour les détails de la journée
    if (showDetailsModal && selectedDay != null) {
        AlertDialog(
            onDismissRequest = { showDetailsModal = false },
            confirmButton = {
                TextButton(onClick = { showDetailsModal = false }) {
                    Text("Fermer")
                }
            },
            title = { Text("Détails de la journée") },
            text = {
                Column {
                    Text("Date : ${selectedDay!!.date}", style = MaterialTheme.typography.bodyLarge)
                    Text("Heure : ${selectedDay!!.startTime}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Activités suggérées :", style = MaterialTheme.typography.bodyMedium)
                    selectedDay!!.activities.forEach { activity ->
                        Text("- $activity", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        )
    }

    // Affichage de la modale pour confirmer la suppression
    if (showDeleteModal && selectedDay != null) {
        AlertDialog(
            onDismissRequest = { showDeleteModal = false },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        selectedDay?.let { day ->
                            LocalStorage.removePlannedDay(context, day)
                            plannedDays.remove(day)
                        }
                        showDeleteModal = false
                    }
                }) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteModal = false }) {
                    Text("Annuler")
                }
            },
            title = { Text("Suppression de planification") },
            text = {
                Text("Êtes-vous sûr de vouloir supprimer la journée du ${selectedDay!!.date} ? Cette action est irréversible.")
            }
        )
    }
}


