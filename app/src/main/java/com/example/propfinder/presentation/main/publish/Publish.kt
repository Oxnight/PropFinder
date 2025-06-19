package com.example.propfinder.presentation.main.publish

import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

@Composable
fun Publish(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel, navController : NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFD9D9D9)),
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
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
        FormulaireAvance(annonceViewModel = annonceViewModel, authViewModel = authViewModel, navController = navController)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FormulaireAvance(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current

    var selectedOption by remember { mutableStateOf("") }

    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var caracteristiques by remember { mutableStateOf("") }
    var localisation by remember { mutableStateOf("") }

    var coordonnees by remember { mutableStateOf("") }
    var geocodingError by remember { mutableStateOf<String?>(null) }

    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

    var prix by remember { mutableStateOf("") }

    LaunchedEffect(localisation) {
        if (localisation.isNotBlank()) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = withContext(Dispatchers.IO) {
                    geocoder.getFromLocationName(localisation, 1)
                }
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    coordonnees = "${location.latitude},${location.longitude}"
                    geocodingError = null
                } else {
                    coordonnees = ""
                    geocodingError = "Adresse introuvable."
                }
            } catch (e: IOException) {
                coordonnees = ""
                geocodingError = "Erreur de géocodage : ${e.localizedMessage}"
            }
        } else {
            coordonnees = ""
            geocodingError = null
        }
    }

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
                        RadioButton(selected = selectedOption == "à vendre", onClick = null)
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
                        RadioButton(selected = selectedOption == "à louer", onClick = null)
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
                    Text("   (${imageUris.size} images chargées)", fontWeight = FontWeight.Light)
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
                OutlinedTextField(
                    value = prix,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            prix = it
                        }
                    },
                    label = { Text("Prix (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
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
                    label = { Text("Adresse") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                coordonnees.let { coords ->
                    if (coords.isNotBlank()) {
                        Text(
                            text = "Coordonnées : $coords",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }

                geocodingError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        val prixFloat = prix.toFloatOrNull() ?: 0f
                        val isFormValid = titre.isNotBlank()
                                && description.isNotBlank()
                                && caracteristiques.isNotBlank()
                                && localisation.isNotBlank()
                                && prixFloat > 0f
                                && selectedOption.isNotBlank()

                        if (isFormValid) {
                            annonceViewModel.publishAnnonce(
                                titre = titre,
                                description = description,
                                caracteristiques = caracteristiques,
                                localisation = localisation,
                                coordonnees = coordonnees,
                                prix = prixFloat,
                                type = selectedOption,
                                idUser = authViewModel.getUserId() ?: "",
                                imageUri = if (imageUris.isNotEmpty()) imageUris[0] else null
                            ) {
                                Toast.makeText(context, "Annonce publiée avec succès", Toast.LENGTH_SHORT).show()
                                navController.navigate("main")
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