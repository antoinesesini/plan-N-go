package ca.uqac.etu.planngo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import ca.uqac.etu.planngo.navigation.BottomNavigationBar
import ca.uqac.etu.planngo.screens.*
import ca.uqac.etu.planngo.ui.theme.AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    // Constants
    private val PERMISSION_ID = 1010

    // FusedLocationProviderClient for location services
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Mutable states for latitude and longitude
    private var latitude by mutableStateOf(48.4225)
    private var longitude by mutableStateOf(-71.0606)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Request permissions and get the last known location
        requestPermission()
        getLastLocation()

        // Enable immersive mode to hide system UI
        setImmersiveMode()

        setContent {
            AppTheme {
                val navController = rememberNavController()
                var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            selectedIndex = selectedItemIndex,
                            onItemSelected = { selectedItemIndex = it },
                            onFabClick = { selectedItemIndex = 2 }
                        )
                    }
                ) {
                    when (selectedItemIndex) {
                        0 -> MapScreen(latitude, longitude)
                        1 -> ActiviteScreen()
                        2 -> PlanScreen()
                        3 -> ChatBotScreen()
                        4 -> MenuScreen(navController = navController)
                    }
                }
            }
        }
    }

    // Set immersive mode to hide system navigation and status bars
    private fun setImmersiveMode() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) setImmersiveMode()
    }

    // Check if location permissions are granted
    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request location permissions
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    // Check if location services are enabled (GPS or Network)
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Get the last known location
    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                    } else {
                        // Action si la localisation n'est pas disponible
                    }
                }
            } else {
                Toast.makeText(this, "Please Turn on Your device Location", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission()
        }
    }
}
