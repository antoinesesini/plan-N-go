import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uqac.etu.planngo.components.ressources.CustomButton
import ca.uqac.etu.planngo.components.ressources.FirstText
import ca.uqac.etu.planngo.screens.menuScreens.ChatbotContent
import ca.uqac.etu.planngo.screens.menuScreens.HistoriqueContent
import ca.uqac.etu.planngo.screens.menuScreens.PlanifierContent
import ca.uqac.etu.planngo.screens.menuScreens.ReglagesContent

@Preview
@Composable
fun MenuContent() {
    var selectedOption by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 3.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedOption == -1) {
            CustomButton(text = "Historique") { selectedOption = 0 }
            CustomButton(text = "Planifier") { selectedOption = 1 }
            CustomButton(text = "Chat bot") { selectedOption = 2 }
            CustomButton(text = "Réglages") { selectedOption = 3 }
        } else {
            Row {
                IconButton(onClick = { selectedOption = -1 }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.Black)
                }
                when (selectedOption) {
                    0 -> {
                        FirstText("Historique")
                    }
                    1 -> {
                        FirstText("Planifier")
                    }
                    2 -> {
                        FirstText("Chat bot")
                    }
                    3 -> {
                        FirstText("Réglages")
                    }
                }
            }
            // Flèche de retour


            // Afficher le contenu basé sur l'option sélectionnée
            when (selectedOption) {
                0 -> {
                    HistoriqueContent()
                }
                1 -> {
                    PlanifierContent()
                }
                2 -> {
                    ChatbotContent()
                }
                3 -> {
                    ReglagesContent()
                }
            }
        }
    }
}
