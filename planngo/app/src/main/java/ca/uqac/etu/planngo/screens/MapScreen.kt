package ca.uqac.etu.planngo.screens

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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

@Composable
fun MapScreen(latitude: Double, longitude: Double) {
    Log.d("Debug:", latitude.toString())
    Log.d("Debug:", longitude.toString())
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val marker = remember { Marker(mapView) }

    LaunchedEffect(latitude, longitude) {
        mapView.apply {
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(latitude, longitude))

            // Mettre à jour le marqueur
            marker.position = GeoPoint(latitude, longitude)
            marker.title = "Votre Position"
            marker.icon = ContextCompat.getDrawable(context, R.drawable.location_icon)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Ajuster l'ancrage du marqueur
            // Si le marqueur n'est pas déjà sur la carte, l'ajouter
            if (!overlays.contains(marker)) {
                overlays.add(marker) // Ajoutez le marqueur à la carte
            }

            invalidate() // Réactualiser la carte
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Affichage de la carte OpenStreetMap
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        // Ajout de la barre de recherche par-dessus la carte
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            FloatingSearchBar()
        }
    }
}


