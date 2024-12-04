package ca.uqac.etu.planngo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.etu.planngo.models.DayPlan
import ca.uqac.etu.planngo.data.LocalStorage
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen() {
    // Récupérer le contexte et définir les états
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

    // Conteneur principal avec un alignement central
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Mes planifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            if (plannedDays.isEmpty()) {
                // Message lorsque aucune journée n'est planifiée
                Text(
                    text = "Aucune journée planifiée pour l'instant.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Liste des journées planifiées avec LazyColumn
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(plannedDays.size) { index ->
                        val dayPlan = plannedDays[index]

                        // Carte représentant chaque journée planifiée
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
//                                .background(MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(8.dp))
                                .pointerInput(Unit) {
                                    // Gérer les actions de tap et long press
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
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                // Affichage de la date et de l'heure de la journée
                                Text(
                                    text = "Le ${dayPlan.date}",
                                    style = MaterialTheme.typography.titleMedium,
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
            title = { Text("Détails de la journée", color = MaterialTheme.colorScheme.primary) },
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
            },
            containerColor = MaterialTheme.colorScheme.surface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    // Affichage de la modale pour confirmer la suppression
    if (showDeleteModal && selectedDay != null) {
        AlertDialog(
            onDismissRequest = { showDeleteModal = false },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        // Supprimer la journée planifiée du DataStore et de la liste
                        selectedDay?.let { day ->
                            LocalStorage.removePlannedDay(context, day)
                            plannedDays.remove(day)
                        }
                        showDeleteModal = false
                    }
                }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteModal = false }) {
                    Text("Annuler")
                }
            },
            title = { Text("Suppression de planification", color = MaterialTheme.colorScheme.primary) },
            text = {
                Text("Êtes-vous sûr de vouloir supprimer la journée du ${selectedDay!!.date} ? Cette action est irréversible.")
            },
            containerColor = MaterialTheme.colorScheme.surface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}
