package ca.uqac.etu.planngo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ca.uqac.etu.planngo.ui.theme.AppTheme
import android.preference.PreferenceManager
import androidx.compose.runtime.*
import org.osmdroid.config.Configuration
import androidx.compose.runtime.saveable.rememberSaveable
import ca.uqac.etu.planngo.navigation.BottomNavigationBar
import ca.uqac.etu.planngo.screens.MapScreen
import ca.uqac.etu.planngo.screens.MenuScreen


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                        0 -> MapScreen()  // Page de la carte
                        1 -> MenuScreen()  // Page du menu
                    }
                }
            }
        }
    }
}