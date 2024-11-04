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
import ca.uqac.etu.planngo.screens.menuScreens.DiscussionsScreen
import ca.uqac.etu.planngo.screens.menuScreens.InvitationsScreen
import ca.uqac.etu.planngo.screens.menuScreens.LocationsScreen
import ca.uqac.etu.planngo.screens.menuScreens.LogoutScreen
import ca.uqac.etu.planngo.screens.menuScreens.MyActivitiesScreen
import ca.uqac.etu.planngo.screens.menuScreens.NotificationsScreen
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
            composable("invitations") { InvitationsScreen() }
            composable("locations") { LocationsScreen() }
            composable("my_activities") { MyActivitiesScreen() }
            composable("create_activity") { CreateActivityScreen() }
            composable("discussions") { DiscussionsScreen() }
            composable("notifications") { NotificationsScreen() }
            composable("preferences") { PreferencesScreen() }
            composable("logout") { LogoutScreen() }
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
                    title = "Calendrier",
                    onClick = { innerNavController.navigate("calendar") }
                )
                SettingsItem(
                    icon = Icons.Filled.Group,
                    title = "Invitations",
                    onClick = { innerNavController.navigate("invitations") }
                )
                SettingsItem(
                    icon = Icons.Filled.Place,
                    title = "Lieux",
                    onClick = { innerNavController.navigate("locations") }
                )
            }
        }

        item {
            SettingsSection(title = "Gestion des Activités") {
                SettingsItem(
                    icon = Icons.Filled.List,
                    title = "Mes Activités",
                    onClick = { innerNavController.navigate("my_activities") }
                )
                SettingsItem(
                    icon = Icons.Filled.Add,
                    title = "Créer une Activité",
                    onClick = { innerNavController.navigate("create_activity") }
                )
            }
        }

        item {
            SettingsSection(title = "Communication") {
                SettingsItem(
                    icon = Icons.Filled.Chat,
                    title = "Discussions",
                    onClick = { innerNavController.navigate("discussions") }
                )
                SettingsItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notifications",
                    onClick = { innerNavController.navigate("notifications") }
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
                SettingsItem(
                    icon = Icons.Filled.ExitToApp,
                    title = "Déconnexion",
                    onClick = { innerNavController.navigate("logout") }
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
