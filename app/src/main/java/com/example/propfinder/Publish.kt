package com.example.propfinder

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Publish() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFD9D9D9)),
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "  Publier une annonce",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
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
        Divider(
            color = Color(0xFF1E1E1E),
            thickness = 3.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )

        FormulaireAvance()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FormulaireAvance() {
    val context = LocalContext.current

    val options = listOf("à vendre", "à louer")
    var selectedOption by remember { mutableStateOf("") }

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

    val scrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Vous avez un bien :",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectable(
                                selected = selectedOption == "à vendre",
                                onClick = { selectedOption = "à vendre" },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == "à vendre",
                            onClick = null // null ici car l'action est gérée par Row/selectable
                        )
                        Text("à vendre", modifier = Modifier.padding(start = 8.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectable(
                                selected = selectedOption == "à louer",
                                onClick = { selectedOption = "à louer" },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == "à louer",
                            onClick = null
                        )
                        Text("à louer", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = titre,
                    onValueChange = { titre = it },
                    label = { Text("Titre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            item {
                Text(
                    text = "Ajouter des images :",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row (
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF07B42))
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter des images",
                            tint = Color(0xFFD9D9D9),
                        )
                    }
                    Text("   (${imageUris.size} images chargées)",fontWeight = FontWeight.Light)
                }
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text("Prix: ${prix.toInt()} €",fontWeight = FontWeight.Bold)
                Slider(
                    value = prix,
                    onValueChange = { prix = it },
                    valueRange = 0f..1000f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF1E1E1E),
                        activeTrackColor = Color(0xFFF07B42),
                        inactiveTrackColor = Color.LightGray
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = informations,
                    onValueChange = { informations = it },
                    label = { Text("Informations") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = addresse,
                    onValueChange = { addresse = it },
                    label = { Text("Adresse") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            //items(imageUris) { uri ->
                // Tu peux activer ceci avec Coil ou Glide (via rememberAsyncImagePainter ou autre)
                /* Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                ) */
            //}

            item {
                Button(
                    onClick = {
                        val isFormValid = titre.isNotBlank()
                                && description.isNotBlank()
                                && informations.isNotBlank()
                                && addresse.isNotBlank()
                                && prix > 0f
                                && selectedOption.isNotBlank()

                        if (!isFormValid) {
                            Toast.makeText(context, "Merci de remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
                        } else {
                            //envoie des données à firebase idk
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF07B42), // fond orange clair
                        contentColor = Color.White            // texte blanc
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Envoyer", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}