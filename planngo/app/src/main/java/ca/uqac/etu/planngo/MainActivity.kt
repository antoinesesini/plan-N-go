package ca.uqac.etu.planngo

import MenuContent
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ca.uqac.etu.planngo.ui.theme.AppTheme
import android.preference.PreferenceManager
import androidx.compose.runtime.*
import org.osmdroid.config.Configuration
import androidx.compose.runtime.saveable.rememberSaveable
import ca.uqac.etu.planngo.navigation.BottomNavigationBar
import ca.uqac.etu.planngo.screens.MapScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {

                Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

                val scope = rememberCoroutineScope()

                // Page sélectionnée actuellement (0: Map, 1: BottomSheet Menu)
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }

                // State to manage the visibility of the BottomSheet
                val bottomSheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
                var showBottomSheet by remember { mutableStateOf(false) }

                //Interface générale
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            selectedIndex = selectedItemIndex,
                            onItemSelected = { selectedIndex ->
                                if (selectedIndex == 1) {
                                    // Trigger BottomSheet for Menu
                                    showBottomSheet = true
                                    scope.launch {
                                        bottomSheetState.show()  // Show BottomSheet
                                    }
                                } else {
                                    // Switch to MapScreen
                                    selectedItemIndex = selectedIndex
                                }
                            }
                        )
                    }
                ) {
                    // Affiche la carte par défaut
                    MapScreen()

                    // Affichage du BottomSheet
                    if (showBottomSheet) {
                        ModalBottomSheet(
                            sheetState = bottomSheetState,
                            onDismissRequest = {
                                scope.launch { bottomSheetState.hide() }
                                showBottomSheet = false
                            }
                        ) {
                            // Contenu du Menu dans le BottomSheet
                            MenuContent()
                        }
                    }
                }
            }
        }
    }
}
