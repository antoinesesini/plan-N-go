package ca.uqac.etu.planngo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.uqac.etu.planngo.screens.menuScreens.CalendarScreen
import ca.uqac.etu.planngo.screens.menuScreens.CreateActivityScreen
import ca.uqac.etu.planngo.screens.menuScreens.PreferencesScreen

@Composable
fun MenuScreen(navController: NavController) {
    val innerNavController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // NavHost pour afficher les sous-écrans
        NavHost(navController = innerNavController, startDestination = "menu") {
            composable("menu") {
                MenuContent(innerNavController)
            }
            composable("calendar") { CalendarScreen(navController = innerNavController) }
            composable("create_activity") { CreateActivityScreen(navController = innerNavController) }
            composable("preferences") { PreferencesScreen() }
        }
    }
}

@Composable
fun MenuContent(innerNavController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            Text(
                text = "Plan N'Go",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
        }

        item {
            SettingsSection(title = "Planification") {
                SettingsItem(
                    icon = Icons.Filled.CalendarToday,
                    title = "Calendrier de mes planifications",
                    onClick = { innerNavController.navigate("calendar") }
                )
            }
        }

        item {
            SettingsSection(title = "Gestion des Activités") {
                SettingsItem(
                    icon = Icons.Filled.Add,
                    title = "Créer une Activité",
                    onClick = { innerNavController.navigate("create_activity") }
                )
            }
        }
        item {
            SettingsSection(title = "Paramètres") {
                SettingsItem(
                    icon = Icons.Filled.Settings,
                    title = "Préférences",
                    onClick = { innerNavController.navigate("preferences") }
                )
            }
        }
    }
}


@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp)
    ) {
        content()
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit, hasToggle: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        if (hasToggle) {
            Switch(
                checked = true,
                onCheckedChange = {}
            )
        }
    }
}
