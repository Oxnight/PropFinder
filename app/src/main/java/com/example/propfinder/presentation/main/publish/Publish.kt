package com.example.propfinder.presentation.main.publish

import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

@Composable
fun Publish(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel, navController: NavController) {
    // Conteneur principal de la page de publication
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFD9D9D9))
    ) {
        // En-tête avec icône maison et titre
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

        // Séparateur visuel
        Divider(
            color = Color(0xFF1E1E1E),
            thickness = 3.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )

        // Inclusion du formulaire de publication
        FormulaireAvance(annonceViewModel, authViewModel, navController)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FormulaireAvance(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current

    // États pour les champs du formulaire
    var selectedOption by remember { mutableStateOf("") }
    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var caracteristiques by remember { mutableStateOf("") }
    var localisation by remember { mutableStateOf("") }
    var coordonnees by remember { mutableStateOf("") }
    var geocodingError by remember { mutableStateOf<String?>(null) }
    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    var prix by remember { mutableStateOf("") }

    // Sélecteur d’images à partir de la galerie
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> -> imageUris = uris }

    // Géocodage automatique basé sur l’adresse saisie
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

    // Bloc principal scrollable contenant le formulaire
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Type d'annonce : à vendre / à louer
            Text("Vous avez un bien :", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    .testTag("radio_louer")
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

            // Titre de l’annonce
            OutlinedTextField(
                value = titre,
                onValueChange = { titre = it },
                label = { Text("Titre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("titre_field")
            )

            // Ajout d’images
            Text("Ajouter des images :", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                        Icons.Default.Add,
                        contentDescription = "Ajouter des images",
                        tint = Color(0xFFD9D9D9),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { imagePicker.launch("image/*") }
                            .testTag("add_image_button")
                    )
                }
                Text("   (${imageUris.size} images chargées)", fontWeight = FontWeight.Light)
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().testTag("description_field")
            )

            // Prix
            OutlinedTextField(
                value = prix,
                onValueChange = {
                    if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        prix = it
                    }
                },
                label = { Text("Prix (€)") },
                modifier = Modifier.fillMaxWidth().testTag("prix_field"),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            // Caractéristiques du bien
            OutlinedTextField(
                value = caracteristiques,
                onValueChange = { caracteristiques = it },
                label = { Text("Caractéristiques") },
                modifier = Modifier.fillMaxWidth().testTag("carateristiques_field")
            )

            // Adresse du bien
            OutlinedTextField(
                value = localisation,
                onValueChange = { localisation = it },
                label = { Text("Adresse") },
                modifier = Modifier.fillMaxWidth().testTag("adress_field")
            )

            // Affichage des coordonnées géographiques si disponibles
            coordonnees.let { coords ->
                if (coords.isNotBlank()) {
                    Text("Coordonnées : $coords", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }

            // Affichage des erreurs de géocodage
            geocodingError?.let { error ->
                Text(error, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            // Bouton de validation et publication
            Button(
                onClick = {
                    val prixFloat = prix.toFloatOrNull() ?: 0f
                    val isFormValid = titre.isNotBlank() && description.isNotBlank() && caracteristiques.isNotBlank() &&
                            localisation.isNotBlank() && prixFloat > 0f && selectedOption.isNotBlank()

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
                            imageUri = imageUris.firstOrNull()
                        ) {
                            Toast.makeText(context, "Annonce publiée avec succès", Toast.LENGTH_SHORT).show()
                            navController.navigate("main")
                        }
                    } else {
                        Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.testTag("submit_add_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF07B42),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Envoyer")
            }
        }
    }
}