package ca.uqac.etu.planngo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import ca.uqac.etu.planngo.ui.theme.AppTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHost
import ca.uqac.etu.planngo.navigation.BottomNavigationBar
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import ca.uqac.etu.planngo.screens.*

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
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
                ) { paddingValues ->
                    when (selectedItemIndex) {
                        0 -> MapScreen()
                        1 -> ActiviteScreen()
                        2 -> PlanScreen()
                        3 -> ChatBotScreen()
                        4 -> MenuScreen(navController = navController)
                    }
                }


            }
        }
    }
}

