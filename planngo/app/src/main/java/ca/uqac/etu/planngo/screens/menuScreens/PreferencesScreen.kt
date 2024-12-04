package ca.uqac.etu.planngo.screens.menuScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uqac.etu.planngo.data.LocalStorage
import kotlinx.coroutines.launch

// Écran des préférences utilisateur
@Composable
fun PreferencesScreen(
    navController: NavController,
    darkTheme: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit,
) {
    // Variables d'état pour le mode sombre et la boîte de dialogue de confirmation
    var isDarkTheme by remember { mutableStateOf(darkTheme) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope() // CoroutineScope pour les actions asynchrones
    val context = LocalContext.current // Contexte local pour l'accès à LocalStorage

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            // En-tête avec bouton retour et titre
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Préférences et réglages",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Option pour activer/désactiver le mode sombre
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Mode sombre", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isChecked ->
                            isDarkTheme = isChecked
                            onDarkThemeToggle(isDarkTheme)
                        }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Bouton pour effacer les données locales
            item {
                Button(
                    onClick = { showConfirmationDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = "Effacer les données locales")
                }
            }
        }
    }

    // Affichage de la boîte de dialogue de confirmation de suppression des données
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            confirmButton = {
                // Action de confirmation pour effacer les données
                TextButton(onClick = {
                    scope.launch {
                        LocalStorage.clearAllData(context)
                        showConfirmationDialog = false
                    }
                }) {
                    Text(
                        text = "Confirmer",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                // Bouton d'annulation
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text(text = "Annuler")
                }
            },
            title = { Text("Effacement des données") },
            text = {
                Text(
                    "Êtes-vous sûr de vouloir effacer toutes les données locales ? Cette action est irréversible.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        )
    }
}
