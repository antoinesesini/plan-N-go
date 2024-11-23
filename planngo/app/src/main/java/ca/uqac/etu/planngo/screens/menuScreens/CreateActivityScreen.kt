package ca.uqac.etu.planngo.screens.menuScreens

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uqac.etu.planngo.data.AddressToCoordinates
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.models.ActivityType
import ca.uqac.etu.planngo.viewmodel.ActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.Calendar

@Composable
fun CreateActivityScreen(activityViewModel: ActivityViewModel = viewModel(), navController: NavController) {
    // Variables d'état pour le formulaire
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ActivityType.MARCHE) }
    var address by remember { mutableStateOf("") }
    var duration by remember { mutableIntStateOf(1) }
    var difficulty by remember { mutableIntStateOf(1) }
    val requiredItems = remember { mutableStateListOf<String>() }
    var newItem by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("") }
    var endHour by remember { mutableStateOf("") }

    //Dialogues d'affichage
    var showAddressErrorDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun validateFields(): Boolean {
        if (name.isBlank() || startHour.isBlank() || endHour.isBlank() || address.isBlank()) {
            return false
        }
        return true
    }

    // Fonction pour ajouter une activité
    fun createActivity() {

        if (!validateFields()) {
            showErrorDialog = true
            return
        }

        // Utiliser la fonction getAdresseFromCoordinates pour obtenir les coordonnées de localisation
        CoroutineScope(Dispatchers.Main).launch {
            val coordinates: Pair<Double,Double>? = AddressToCoordinates().getCoordinatesFromAddress(address)
            if (coordinates == null) {
                showAddressErrorDialog = true
            } else {
                val location = GeoPoint(coordinates.first, coordinates.second)

                val newActivity = Activity(
                    name = name,
                    type = type,
                    description = description,
                    location = location,
                    hours = mapOf("start" to startHour, "end" to endHour),
                    duration = duration,
                    difficulty = difficulty,
                    required = requiredItems,
                    pictures = listOf()
                )
                activityViewModel.addActivity(newActivity)
                showConfirmationDialog = true
            }
        }


    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Créer une Activité",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            item { // Champ pour le nom de l'activité
                Text("Informations et localisation", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de l'activité") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Champ pour la description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sélection du type d'activité
                DropdownMenuActivityType(type) { selectedType ->
                    type = selectedType
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Champ pour l'adresse
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))


                // Duration Slider
                Slider(
                    value = duration.toFloat(),
                    onValueChange = { duration = it.toInt() },
                    valueRange = 1f..12f,
                    steps = 11,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Durée (heures) : $duration")

                Spacer(modifier = Modifier.height(16.dp))

                // Champ pour la difficulté
                Slider(
                    value = difficulty.toFloat(),
                    onValueChange = { difficulty = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Difficulté : $difficulty")

                Spacer(modifier = Modifier.height(16.dp))

                // Heure d'ouverture
                Text("Horaires", style = MaterialTheme.typography.titleMedium)

                Button(onClick = {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                            startHour = formattedTime
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true // 24-hour format
                    ).show()
                }) {
                    Text(if (startHour.isEmpty()) "Heure d'ouverture" else "Ouverture : $startHour")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                            endHour = formattedTime
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true // 24-hour format
                    ).show()
                }) {
                    Text(if (endHour.isEmpty()) "Heure de fermeture" else "Fermeture : $endHour")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Liste des éléments requis
                Text("Que faut-il apporter ?", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newItem,
                        onValueChange = { newItem = it },
                        label = { Text("Ajouter un élément") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newItem.isNotBlank()) {
                            requiredItems.add(newItem)
                            newItem = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Affichage des éléments requis ajoutés
                requiredItems.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = item, modifier = Modifier.weight(1f))
                        IconButton(onClick = { requiredItems.remove(item) }) {
                            Icon(Icons.Default.Close, contentDescription = "Supprimer")
                        }
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { createActivity() }
                    ) {
                        Text("Créer l'activité")
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Confirmation") },
                text = { Text("L'activité a été ajoutée avec succès.") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmationDialog = false
                        navController.navigateUp() // Naviguer en arrière après confirmation
                    }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Erreur") },
                text = { Text("Veuillez remplir tous les champs obligatoires : nom, horaires, durée et localisation") },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showAddressErrorDialog) {
            AlertDialog(
                onDismissRequest = { showAddressErrorDialog = false },
                title = { Text("Erreur") },
                text = { Text("Impossible de trouver les coordonnées pour l'adresse saisie.") },
                confirmButton = {
                    TextButton(onClick = { showAddressErrorDialog = false }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DropdownMenuActivityType(
    selectedType: ActivityType,
    onTypeSelected: (ActivityType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedType.name)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ActivityType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                    onTypeSelected(type)
                    expanded = false }
                )
            }
        }
    }
}
