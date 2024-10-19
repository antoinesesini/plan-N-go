package ca.uqac.etu.planngo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import ca.uqac.etu.planngo.R

@Composable
fun FloatingSearchBar() {
    var query by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.medium)
        ) {
            Image(
                painter = painterResource(id = R.drawable.custom_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp)
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Rechercher des activités") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                singleLine = true
            )
        }
    }
}
