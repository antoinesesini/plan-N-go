package ca.uqac.etu.planngo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import ca.uqac.etu.planngo.R

@Composable
fun FloatingSearchBar() {
    // Liste des activités avec des catégories
    val activities = listOf(
        Activity("Randonnée", "Sport"),
        Activity("Ski", "Sport"),
        Activity("Musée", "Culture"),
        Activity("Théâtre", "Culture"),
        Activity("Escalade", "Sport"),
        Activity("Concert", "Divertissement")
    )

    // Liste des catégories pour le filtrage
    val categories = listOf("Sport", "Culture", "Divertissement")
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) } // Pour le menu déroulant des catégories

    // Filtrer les activités en fonction de la recherche et de la catégorie sélectionnée
    val filteredActivities = activities.filter { activity ->
        (selectedCategory == null || activity.category == selectedCategory) &&
                activity.name.contains(query.text, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            // Barre de recherche
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.medium)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.custom_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(32.dp)
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Rechercher des activités") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp),
                    singleLine = true
                )
            }

            // Menu déroulant pour filtrer par catégorie
            Row(modifier = Modifier.padding(top = 8.dp)) {
                TextButton(onClick = { expanded = true }) {
                    Text(text = selectedCategory ?: "Toutes les catégories")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            },
                            text = { Text(category) }
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            selectedCategory = null // Réinitialiser le filtre
                            expanded = false
                        },
                        text = { Text("Toutes les catégories") }
                    )
                }
            }

            // Affichage des activités filtrées
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(filteredActivities) { activity ->
                    ActivityItem(activity)
                }
            }
        }
    }
}

@Composable
fun ActivityItem(activity: Activity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(text = activity.name, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = activity.category, style = MaterialTheme.typography.bodySmall)
    }
}

// Exemple de classe pour représenter une activité
data class Activity(
    val name: String,
    val category: String
)