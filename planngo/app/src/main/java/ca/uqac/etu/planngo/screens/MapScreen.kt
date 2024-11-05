package ca.uqac.etu.planngo.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import ca.uqac.etu.planngo.R
import ca.uqac.etu.planngo.components.FloatingSearchBar
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Marker


data class Activity(
    val title: String,
    val description: String,
    val schedule: String,
    val location: GeoPoint,
    val type: ActivityType
)

enum class ActivityType { MARCHE, SKI, FITNESS, RANDONNEE, KAYAK, FOOTBALL_AMERICAIN, SOCCER }

// Fonction pour obtenir l'icône selon le type d'activité
fun getIconForType(type: ActivityType): Int {
    return when (type) {
        ActivityType.MARCHE -> R.drawable.walking_icon
        ActivityType.SKI -> R.drawable.skiing_icon
        ActivityType.FITNESS -> R.drawable.fitness_icon
        ActivityType.RANDONNEE -> R.drawable.hiking_icon
        ActivityType.KAYAK -> R.drawable.kayaking_icon
        ActivityType.FOOTBALL_AMERICAIN -> R.drawable.football_icon
        ActivityType.SOCCER -> R.drawable.soccer_icon
    }
}


// Liste d'activités avec différents types
val activities = listOf(
    Activity("Balade du matin", "Marche le long de la rivière Saguenay", "07:00 - 08:30", GeoPoint(48.4220, -71.0650), ActivityType.MARCHE),
    Activity("Ski alpin", "Journée de ski au Mont Édouard", "09:00 - 16:00", GeoPoint(48.3400, -70.8923), ActivityType.SKI),
    Activity("Séance de fitness", "Entraînement en plein air", "10:00 - 11:00", GeoPoint(48.4300, -71.0500), ActivityType.FITNESS),
    Activity("Randonnée en montagne", "Exploration du Mont Valin", "08:00 - 14:00", GeoPoint(48.6667, -71.0654), ActivityType.RANDONNEE),
    Activity("Kayak en rivière", "Descente en kayak sur la rivière", "13:00 - 16:00", GeoPoint(48.5000, -70.9500), ActivityType.KAYAK),
    Activity("Match de football américain", "Match local au stade Saguenay", "15:00 - 18:00", GeoPoint(48.4320, -71.0520), ActivityType.FOOTBALL_AMERICAIN),
    Activity("Partie de soccer", "Tournoi de soccer pour adultes", "17:00 - 19:00", GeoPoint(48.4180, -71.0600), ActivityType.SOCCER),
    Activity("Marche au crépuscule", "Balade dans le parc de la Colline", "18:00 - 19:00", GeoPoint(48.4260, -71.0574), ActivityType.MARCHE),
    Activity("Ski de fond", "Parcours de ski de fond à Saint-Fulgence", "10:00 - 14:00", GeoPoint(48.4330, -70.9820), ActivityType.SKI),
    Activity("Session de fitness", "Entraînement de HIIT en plein air", "12:00 - 12:30", GeoPoint(48.4190, -71.0610), ActivityType.FITNESS),
    Activity("Randonnée en forêt", "Sentier des Amoureux", "09:00 - 11:00", GeoPoint(48.4155, -71.0750), ActivityType.RANDONNEE),
    Activity("Kayak du soir", "Sortie en kayak au coucher du soleil", "18:00 - 20:00", GeoPoint(48.4305, -71.0605), ActivityType.KAYAK),
    Activity("Tournoi de football américain", "Tournoi annuel", "14:00 - 18:00", GeoPoint(48.4350, -71.0800), ActivityType.FOOTBALL_AMERICAIN),
    Activity("Séance de soccer", "Match amical entre amis", "16:00 - 17:30", GeoPoint(48.4285, -71.0530), ActivityType.SOCCER),
    Activity("Marche avec vue", "Marche au bord du lac Kenogami", "15:00 - 16:30", GeoPoint(48.3760, -71.1200), ActivityType.MARCHE)
)


@Composable
fun MapScreen(latitude: Double, longitude: Double) {

    Log.d("Debug:", latitude.toString())
    Log.d("Debug:", longitude.toString())
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }


    //Rendu de départ
    LaunchedEffect(latitude, longitude) {
        mapView.apply {
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(latitude, longitude))


            val userLocationMarker = Marker(this).apply {
                position = GeoPoint(latitude, longitude)
                title = "Votre position"
                icon = ContextCompat.getDrawable(context, R.drawable.location_icon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            overlays.remove(userLocationMarker)
            overlays.add(userLocationMarker)


            // Ajout de chaque activité comme marqueur avec icône spécifique
            activities.forEach { activity ->
                val marker = Marker(this).apply {
                    position = activity.location
                    title = activity.title
                    icon = ContextCompat.getDrawable(context, getIconForType(activity.type))
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        selectedActivity = activity
                        true
                    }
                }
                overlays.add(marker)
            }

        }
    }









    //Rendu
    Box(modifier = Modifier.fillMaxSize()) {
        // Affichage de la carte OpenStreetMap
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        // Affichage de la barre de recherche
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            FloatingSearchBar()
        }

        // Affichage de la modale si une activité est sélectionnée
        selectedActivity?.let { activity ->
            AlertDialog(
                onDismissRequest = { selectedActivity = null },
                title = { Text(activity.title) },
                text = {
                    Text("Description : ${activity.description}\n" +
                            "Horaires : ${activity.schedule}")
                },
                confirmButton = {
                    Button(onClick = { selectedActivity = null }) {
                        Text("Fermer")
                    }
                }
            )
        }
    }
}
