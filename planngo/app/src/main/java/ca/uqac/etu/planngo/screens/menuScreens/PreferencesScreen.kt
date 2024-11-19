package ca.uqac.etu.planngo.screens.menuScreens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.etu.planngo.screens.SettingsItem
import ca.uqac.etu.planngo.screens.SettingsSection


@Composable
fun PreferencesScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(
                text = "Préférences",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
        }

        item {
            SettingsSection(title = "Général") {
                SettingsItem(
                    icon = Icons.Filled.LocationOn,
                    title = "Accès aux données de localisation",
                    onClick = {},
                    hasToggle = true
                )
            }
        }

        item {
            SettingsSection(title = "Personnalisation") {
                SettingsItem(
                    icon = Icons.Filled.Brightness4,
                    title = "Mode sombre",
                    onClick = {},
                    hasToggle = true
                )
                SettingsItem(
                    icon = Icons.Filled.Language,
                    title = "Langue de l'application",
                    onClick = {}
                )
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}