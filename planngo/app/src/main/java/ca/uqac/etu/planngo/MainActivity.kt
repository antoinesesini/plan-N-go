package ca.uqac.etu.planngo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ca.uqac.etu.planngo.ui.theme.AppTheme
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.runtime.*
import org.osmdroid.config.Configuration
import androidx.compose.runtime.saveable.rememberSaveable
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

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()
        getLastLocation()

        setContent {
            AppTheme {

                Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

                //Page sélectionnée actuellement
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }

                //Interface générale
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            selectedIndex = selectedItemIndex,
                            onItemSelected = { selectedItemIndex = it }
                        )
                    }
                ) {
                    when (selectedItemIndex) {
                        0 -> MapScreen(latitude, longitude)  // Page de la carte
                        1 -> MenuScreen()  // Page du menu
                    }
                }
            }
        }
    }

    private fun checkPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }

    private fun requestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
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