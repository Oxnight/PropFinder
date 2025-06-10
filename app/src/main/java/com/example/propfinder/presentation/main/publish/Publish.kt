package com.example.propfinder.presentation.main.publish

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel

@Composable
fun Publish(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFD9D9D9)),
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
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
        FormulaireAvance(annonceViewModel = annonceViewModel, authViewModel = authViewModel)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FormulaireAvance(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    var selectedOption by remember { mutableStateOf("") }

    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var caracteristiques by remember { mutableStateOf("") }
    var localisation by remember { mutableStateOf("") }

    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

    var prix by remember { mutableStateOf(50f) }


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
                            onClick = null
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
                        .padding(bottom = 8.dp),

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
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF07B42)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter des images",
                            tint = Color(0xFFD9D9D9),
                        )
                    }
                    Text(
                        "   (${imageUris.size} images chargées)",
                        fontWeight = FontWeight.Light
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),

                )
            }

            item {
                Text("Prix: ${prix.toFloat()} €", fontWeight = FontWeight.Bold)
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
                    ),

                )
            }

            item {
                OutlinedTextField(
                    value = caracteristiques,
                    onValueChange = { caracteristiques = it },
                    label = { Text("Caractéristiques") },
                    modifier = Modifier.fillMaxWidth(),

                )
            }

            item {
                OutlinedTextField(
                    value = localisation,
                    onValueChange = { localisation = it },
                    label = { Text("Localisation") },
                    modifier = Modifier.fillMaxWidth(),

                )
            }

            item {
                Button(
                    onClick = {
                        val isFormValid = titre.isNotBlank()
                                && description.isNotBlank()
                                && caracteristiques.isNotBlank()
                                && localisation.isNotBlank()
                                && prix > 0f
                                && selectedOption.isNotBlank()

                        if (isFormValid) {
                            annonceViewModel.publishAnnonce(
                                titre = titre,
                                description = description,
                                caracteristiques = caracteristiques,
                                localisation = localisation,
                                prix = prix,
                                type = selectedOption,
                                idUser = authViewModel.getUserId() ?: "",
                                imageUri = if (imageUris.isNotEmpty()) imageUris[0] else null
                            ) {
                                Toast.makeText(context, "Annonce publiée avec succès", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                        }

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF07B42),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),

                ) {
                    Text(text = "Envoyer")
                }
            }
        }
    }
}