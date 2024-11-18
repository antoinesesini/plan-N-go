package ca.uqac.etu.planngo.screens.menuScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.models.ActivityType
import ca.uqac.etu.planngo.viewmodel.ActivityViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun CreateActivityScreen(activityViewModel: ActivityViewModel = viewModel(), navController: NavController) {
    // Variables d'état pour le formulaire
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ActivityType.MARCHE) }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var difficulty by remember { mutableIntStateOf(1) }
    val requiredItems = remember { mutableStateListOf<String>() }
    var newItem by remember { mutableStateOf("") }

    // Variables pour les heures d'ouverture
    var startHour by remember { mutableStateOf("") }
    var endHour by remember { mutableStateOf("") }

    // Variables pour la direction (N, S, E, O)
    var directionLatitude by remember { mutableStateOf("N") }
    var directionLongitude by remember { mutableStateOf("E") }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    fun validateFields(): Boolean {
        if (name.isBlank() || startHour.isBlank() || endHour.isBlank() || duration.isBlank()) {
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

        val finalLatitude = (latitude.toDoubleOrNull() ?: 0.0) * if (directionLatitude == "S") -1 else 1
        val finalLongitude = (longitude.toDoubleOrNull() ?: 0.0) * if (directionLongitude == "O") -1 else 1
        val location = GeoPoint(finalLatitude, finalLongitude)

        val newActivity = Activity(
            name = name,
            type = type,
            description = description,
            location = location,
            hours = mapOf("start" to startHour, "end" to endHour),
            duration = duration.toIntOrNull() ?: 0,
            difficulty = difficulty,
            required = requiredItems,
            pictures = listOf()
        )
        activityViewModel.addActivity(newActivity)
        showConfirmationDialog = true
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                // Barre d'outils personnalisée avec flèche de retour
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

                // Champs pour les coordonnées (latitude et longitude)
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { newLatitude ->
                        latitude = newLatitude
                                    },
                    label = { Text("Latitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.width(8.dp))

                DropdownMenuDirectionLatitude(
                    selectedDirection = directionLatitude,
                    onDirectionSelected = { newDirection -> directionLatitude = newDirection }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = longitude,
                    onValueChange = { newLongitude ->
                        longitude = newLongitude
                                    },
                    label = { Text("Longitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.width(8.dp))

                DropdownMenuDirectionLongitude(
                    selectedDirection = directionLongitude,
                    onDirectionSelected = { newDirection -> directionLongitude = newDirection }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Champ pour la durée
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Durée (en heures)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

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

                // Heures d'ouverture
                Text("Horaires", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = startHour,
                    onValueChange = { startHour = it },
                    label = { Text("Heure d'ouverture (ex : 09:00)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // Change à Text
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = endHour,
                    onValueChange = { endHour = it },
                    label = { Text("Heure de fermeture (ex : 18:00)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // Change à Text
                    modifier = Modifier.fillMaxWidth()
                )

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

@Composable
fun DropdownMenuDirectionLatitude(
    selectedDirection: String,
    onDirectionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = !expanded },
            //modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedDirection)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("N", "S").forEach { direction ->
                DropdownMenuItem(
                    text = {Text(direction) },
                    onClick = {
                    onDirectionSelected(direction)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun DropdownMenuDirectionLongitude(
    selectedDirection: String,
    onDirectionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = !expanded },
            //modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedDirection)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("E", "O").forEach { direction ->
                DropdownMenuItem(
                    text = {Text(direction) },
                    onClick = {
                        onDirectionSelected(direction)
                        expanded = false
                    })
            }
        }
    }
}
