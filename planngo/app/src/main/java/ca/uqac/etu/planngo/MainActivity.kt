package ca.uqac.etu.planngo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import ca.uqac.etu.planngo.ui.theme.AppTheme
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import ca.uqac.etu.planngo.screens.*
import androidx.core.app.ActivityCompat
import ca.uqac.etu.planngo.navigation.BottomNavigationBar
import ca.uqac.etu.planngo.screens.MapScreen
import ca.uqac.etu.planngo.screens.MenuScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSION_ID = 1010
    // Utilisation de MutableState pour latitude et longitude
    private var latitude by mutableStateOf(48.4225)
    private var longitude by mutableStateOf(-71.0606)
    private val authManager = AuthManager()

    private var darkTheme by mutableStateOf(false)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authManager.signInAnonymously()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()
        getLastLocation()

        // Activer le mode immersif pour masquer la barre de navigation
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )

        setContent {
            AppTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()

                // Page sélectionnée actuellement
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }

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
                        3 -> CalendarScreen()
                        4 -> MenuScreen(navController = navController, darkTheme = darkTheme, onDarkThemeToggle = {
                                isChecked -> darkTheme = isChecked
                        })
                    }
                }


            }
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Réactiver le mode immersif quand l'activité regagne le focus
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
        }
    }

    private fun checkPermission():Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled():Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getLastLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {task->
                    val location:Location? = task.result
                    if(location == null){
                        //To do si on arrive pas à récupérer la location (si il n'y a pas de résultat)
                    }else{
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }else{
                Toast.makeText(this,"Please Turn on Your device Location",Toast.LENGTH_SHORT).show()
            }
        }else{
            requestPermission()
        }
    }
}
