package ca.uqac.etu.planngo.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onFabClick: () -> Unit
) {
    val items = listOf(
        BottomNavigationItem(
            title = "Map",
            selectedIcon = Icons.Filled.Map,
            unselectedIcon = Icons.Outlined.Map
        ),
        BottomNavigationItem(
            title = "Activité",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        ),
        BottomNavigationItem(
            title = "ChatBot",
            selectedIcon = Icons.Filled.ChatBubble,
            unselectedIcon = Icons.Outlined.ChatBubble
        ),
        BottomNavigationItem(
            title = "Menu",
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Outlined.Menu
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left two navigation items
            items.take(2).forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { onItemSelected(index) },
                    label = { Text(item.title) },
                    icon = {
                        Icon(
                            imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.width(56.dp)) // Space for FAB

            
            items.drop(2).forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedIndex == index + 3,
                    onClick = { onItemSelected(index + 3) },
                    label = { Text(item.title) },
                    icon = {
                        Icon(
                            imageVector = if (selectedIndex == index + 3) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .size(56.dp)
                .offset(y = (-41).dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Envoyer")
        }
    }
}
