package ca.uqac.etu.planngo.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uqac.etu.planngo.R
import ca.uqac.etu.planngo.components.FloatingSearchBar
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.models.ActivityType
import ca.uqac.etu.planngo.viewmodel.ActivityViewModel
import coil.compose.AsyncImage
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.graphics.Color


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
        ActivityType.VELO -> R.drawable.biking_icon
        ActivityType.ESCALADE -> R.drawable.climbing_icon
        ActivityType.COMBAT -> R.drawable.fighting_icon
        ActivityType.HANDBALL -> R.drawable.handball_icon
        ActivityType.HOCKEY -> R.drawable.hockey_icon
        ActivityType.RUNNING -> R.drawable.running_icon
        ActivityType.CHIEN_DE_TRAINEAU -> R.drawable.sleddog_icon
        ActivityType.MOTONEIGE -> R.drawable.snowmobile_icon
        ActivityType.RAQUETTES -> R.drawable.snowshoeing_icon
        ActivityType.NATATION -> R.drawable.swimming_icon
        ActivityType.TENNIS -> R.drawable.tennis_icon
        ActivityType.VOLLEYBALL-> R.drawable.volleyball_icon
        ActivityType.YOGA -> R.drawable.yoga_icon
        ActivityType.PECHE -> R.drawable.fishing_icon
    }
}


@Composable
fun MapScreen(latitude: Double, longitude: Double) {

    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }
    val userLocationMarker = remember {
        Marker(mapView).apply {
            title = "Votre position"
            icon = ContextCompat.getDrawable(context, R.drawable.location_icon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            setOnMarkerClickListener { _, _ ->
                true  // Retourner true pour empêcher l'interaction
            }
        }
    }
    var zoomLevel by remember { mutableDoubleStateOf(15.00) }
    val activityViewModel: ActivityViewModel = viewModel()
    val activities = activityViewModel.getActivities()



    val iconCache = mutableMapOf<Int, Bitmap>()
    fun getCircularIconWithBackground(drawableRes: Int, context: Context): Bitmap {
        // Vérifiez si l'icône est déjà dans le cache
        iconCache[drawableRes]?.let { return it }

        val drawable = ContextCompat.getDrawable(context, drawableRes) ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val size = maxOf(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dessiner un dégradé radial vert
        val radius = size / 2f
        val gradient = RadialGradient(
            radius,
            radius,
            radius,
            android.graphics.Color.parseColor("#A8E6CF"),
            android.graphics.Color.parseColor("#56AB2F"),
            Shader.TileMode.CLAMP
        )

        val paint = Paint().apply {
            isAntiAlias = true
            shader = gradient
        }
        canvas.drawCircle(radius, radius, radius, paint)

        // Dessiner l'icône au centre
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)

        // Ajouter l'icône générée dans le cache
        iconCache[drawableRes] = bitmap

        return bitmap
    }




    // Rendu
    LaunchedEffect(latitude, longitude) {
        mapView.apply {

            //Gestion des marqueurs
            fun markerManager() {
                overlays.clear()
                // Ajout de chaque activité comme marqueur avec icône spécifique seulement si le niveau de zoom le permet
                if (zoomLevel >= minimalZoomLevel) {
                    activities.forEach { activity ->
                        val circularIcon = getCircularIconWithBackground(getIconForType(activity.type), context)
                        val marker = Marker(this).apply {
                            position = activity.location
                            title = activity.name
                            icon = BitmapDrawable(context.resources, circularIcon)
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

            //Quelques autres paramètres de la carte
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


    // Rendu de la carte
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier =
            Modifier
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Barre de recherche
            FloatingSearchBar()

           //// Bouton d'ajout d'activité (en haut à droite)
           //Icon(
           //    imageVector = Icons.Filled.Add,  // Icône d'ajout
           //    contentDescription = "Ajouter une activité",
           //    modifier = Modifier
           //        .size(36.dp)
           //        .align(Alignment.End)  // Positionné en haut à droite
           //        .clickable {}
           //)
        }

        selectedActivity?.let { activity ->

            val pagerState = rememberPagerState(pageCount = { activity.pictures.size })

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                        .fillMaxWidth(0.85f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icône de fermeture
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x66000000))
                                    .clickable { selectedActivity = null }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Fermer",
                                    modifier = Modifier.size(32.dp),
                                    tint = Color.White
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
                            tint = Color.Unspecified
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
                                            modifier = Modifier.fillMaxSize(),
                                            error = painterResource(id = R.drawable.custom_logo),
                                            placeholder = painterResource(id = R.drawable.custom_logo)
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
        }
    }
}

