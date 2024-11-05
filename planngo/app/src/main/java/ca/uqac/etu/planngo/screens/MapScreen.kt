package ca.uqac.etu.planngo.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uqac.etu.planngo.data.Activity
import ca.uqac.etu.planngo.data.ActivityRepository
import ca.uqac.etu.planngo.data.ActivityType
import ca.uqac.etu.planngo.viewmodel.ActivityViewModel
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent

const val minimalZoomLevel = 12


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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(latitude: Double, longitude: Double) {

    Log.d("Debug:", latitude.toString())
    Log.d("Debug:", longitude.toString())
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }
    val userLocationMarker = remember {
        Marker(mapView).apply {
            title = "Votre position"
            icon = ContextCompat.getDrawable(context, R.drawable.location_icon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }
    var zoomLevel by remember { mutableDoubleStateOf(15.00) }
    val activityViewModel: ActivityViewModel = viewModel()




    val activities = activityViewModel.getActivities()
    //Rendu de départ
    LaunchedEffect(latitude, longitude) {
        mapView.apply {

            fun markerManager() {
                overlays.clear()
                // Ajout de chaque activité comme marqueur avec icône spécifique seulement si le niveau de zoom le permet
                if (zoomLevel >= minimalZoomLevel) {
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
                userLocationMarker.position = GeoPoint(latitude, longitude)
                overlays.add(userLocationMarker)
            }

            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            setMultiTouchControls(true)
            controller.setZoom(zoomLevel)
            controller.setCenter(GeoPoint(latitude, longitude))

            markerManager()

            mapView.addMapListener(object : MapListener {
                override fun onZoom(event: ZoomEvent?): Boolean {
                    zoomLevel = event?.zoomLevel!!
                    Log.d("Debug:", zoomLevel.toString())
                    markerManager()
                    return true
                }

                override fun onScroll(event: ScrollEvent?): Boolean {
                    return false
                }
            })

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
            BasicAlertDialog(
                onDismissRequest = { selectedActivity = null },
                //buttons = {},
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {

                        // Icone de fermeture en haut à gauche
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Fermer",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { selectedActivity = null }
                            )
                        }

                        // Contenu de la modale
                        Text(
                            text = activity.title,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Description : ${activity.description}",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Horaires : ${activity.schedule}",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
