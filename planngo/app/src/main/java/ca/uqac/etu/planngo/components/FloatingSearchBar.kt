package ca.uqac.etu.planngo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import ca.uqac.etu.planngo.R
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.screens.getIconForType
import ca.uqac.etu.planngo.viewmodel.ActivityViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.pager.rememberPagerState as rememberPagerState1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingSearchBar(viewModel: ActivityViewModel, modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) } // Par défaut fermé
    var selectedActivity by remember { mutableStateOf<Activity?>(null) } // Activité sélectionnée
    val keyboardController = LocalSoftwareKeyboardController.current

    // Observez les activités filtrées depuis le ViewModel
    val filteredActivities by viewModel.filteredActivities.observeAsState(emptyList())

    if (selectedActivity == null) {
        // Afficher la recherche et les résultats
        Column {
            DockedSearchBar(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.searchActivities(it) // Effectuer la recherche
                },
                onSearch = {
                    viewModel.searchActivities(it) // Effectuer la recherche
                },
                active = active,
                onActiveChange = { isActive ->
                    active = isActive
                    if (!isActive) keyboardController?.hide() // Fermer le clavier
                },
                modifier = modifier
                    .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                placeholder = { Text("Rechercher des activités") },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.custom_logo),
                        contentDescription = "Icône du logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                trailingIcon = if (active) {
                    {
                        IconButton(
                            onClick = {
                                query = "" // Réinitialiser la requête
                                active = false // Désactiver l'état actif
                                keyboardController?.hide() // Fermer le clavier
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }
                } else null
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(filteredActivities) { activity ->
                        ActivityListItem(activity = activity) {
                            selectedActivity = activity // Définir l'activité sélectionnée
                            active = false // Fermer la recherche
                            keyboardController?.hide() // Fermer le clavier
                        }
                    }
                }
            }
        }
    } else {
        // Afficher les détails de l'activité sélectionnée
        ActivityDetails(selectedActivity!!) {
            selectedActivity = null // Réinitialiser la sélection pour revenir à la recherche
        }
    }
}

@Composable
fun ActivityDetails(activity: Activity, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        val pagerState = rememberPagerState1(pageCount = { activity.pictures.size })

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icône de fermeture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        .clickable { onBack() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Fermer",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Icône de l'activité
            Icon(
                painter = painterResource(id = getIconForType(activity.type)),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Titre de l'activité
            Text(
                text = activity.name,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Carrousel d'images
            if (activity.pictures.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Carrousel d'images
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                                .background(Color.LightGray)
                        ) {
                            AsyncImage(
                                model = activity.pictures[page],
                                contentDescription = "Image de l'activité",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            )
                        }
                    }

                    // Flèche gauche
                    if (pagerState.currentPage > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Swipe gauche possible",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }

                    // Flèche droite
                    if (pagerState.currentPage < activity.pictures.lastIndex) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "Swipe droite possible",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }




            Spacer(modifier = Modifier.height(16.dp))

            // Informations textuelles de l'activité
            listOf(
                "Description : ${activity.description}",
                "Horaires : ${activity.hours["start"]} - ${activity.hours["end"]}",
                "Durée : ${activity.duration} heure(s)",
                "Difficulté : ${activity.difficulty}/5"
            ).forEach { text ->
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Équipements requis
            if (activity.required.isNotEmpty()) {
                Text(
                    text = "Équipements requis : ${activity.required.joinToString(", ")}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

        }
    }
}

@Composable
fun ActivityListItem(activity: Activity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = getIconForType(activity.type)),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Détails textuels au centre
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
