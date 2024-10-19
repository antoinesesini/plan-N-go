package ca.uqac.etu.planngo.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ca.uqac.etu.planngo.components.FloatingSearchBar
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Affichage de la carte OpenStreetMap
        AndroidView(factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                setMultiTouchControls(true)
                val mapController = controller
                mapController.setZoom(15.0)
                mapController.setCenter(GeoPoint(48.4225, -71.0606))

                // Ajout d'un marqueur sur la carte
                val marker = Marker(this)
                marker.position = GeoPoint(48.4225, -71.0606)
                marker.title = "Chicoutimi"
                overlays.add(marker)
            }
        }, modifier = Modifier.fillMaxSize())

        // Ajout de la barre de recherche par-dessus la carte
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            FloatingSearchBar()
        }
    }
}
