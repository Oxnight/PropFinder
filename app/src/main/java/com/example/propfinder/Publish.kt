package com.example.propfinder

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun Publish() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(androidx.compose.ui.graphics.Color(0xFFD9D9D9)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp) // optionnel : espace avec le haut
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // hauteur fixe pour l'en-tête, tu peux ajuster
            ) {
                // Texte centré horizontalement
                Text(
                    text = "  Publier une annonce",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )

                // Icône à gauche
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(42.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        FormulaireAvance()
    }
}


@Composable
fun FormulaireAvance() {
    val context = LocalContext.current

    val options = listOf("à vendre", "à louer")
    val selections = remember { mutableStateListOf<Boolean>().apply { repeat(options.size) { add(false) } } }

    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var informations by remember { mutableStateOf("") }
    var addresse by remember { mutableStateOf("") }

    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

    var prix by remember { mutableStateOf(50f) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = titre,
            onValueChange = { titre = it },
            label = { Text("Nom") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Choisissez vos options:")
        options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = selections[index],
                        onValueChange = { selections[index] = it }
                    )
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = selections[index],
                    onCheckedChange = { selections[index] = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Ajouter des images")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Affichage des vignettes d'images
        imageUris.forEach { uri ->
            /*Image(
                painter ,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )*/
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Prix: ${prix.toInt()} €")
        Slider(
            value = prix,
            onValueChange = { prix = it },
            valueRange = 0f..1000f,
            steps = 9,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            // Validation simple
            if (titre.isBlank() || description.isBlank()) {
                Toast.makeText(context, "Nom et description requis", Toast.LENGTH_SHORT).show()
            } else {
                val choix = options.filterIndexed { i, _ -> selections[i] }
                Toast.makeText(
                    context,
                    "Nom: $titre\nDesc: $description\nOptions: $choix\nImages: ${imageUris.size}\nPrix: ${prix.toInt()}€",
                    Toast.LENGTH_LONG
                ).show()
            }
        }) {
            Text("Envoyer")
        }
    }
}