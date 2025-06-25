package com.example.propfinder.presentation.main.annonce

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EditAnnonceScreen(
    annonceId: String,
    navController: NavController,
    annonceViewModel: AnnonceViewModel
) {
    val context = LocalContext.current

    // États locaux pour stocker les champs du formulaire
    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prix by remember { mutableStateOf("") }
    var caracteristiques by remember { mutableStateOf("") }
    var localisation by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Chargement des données de l'annonce via son ID
    LaunchedEffect(annonceId) {
        annonceViewModel.getAnnonceById(annonceId) { data ->
            if (data != null) {
                // Pré-remplissage des champs avec les valeurs récupérées
                titre = data["titre"]?.toString() ?: ""
                description = data["description"]?.toString() ?: ""
                prix = data["prix"]?.toString() ?: ""
                caracteristiques = data["caracteristiques"]?.toString() ?: ""
                localisation = data["localisation"]?.toString() ?: ""
            }
            isLoading = false
        }
    }

    // Affichage d'un message de chargement tant que les données ne sont pas prêtes
    if (isLoading) {
        Text("Chargement...", modifier = Modifier.padding(16.dp))
        return
    }

    // Conteneur principal de l'écran avec fond sombre
    Box(modifier = Modifier
        .background(Color(0xFF1E1E1E))
        .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp)) // Bords arrondis
                .background(Color(0xFFD9D9D9)), // Fond clair pour la carte
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre de la page
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "  Modifier l'annonce",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // Séparateur visuel sous le titre
            Divider(
                color = Color(0xFF1E1E1E),
                thickness = 3.dp,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )

            // Formulaire contenant les champs modifiables
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()) // Activation du scroll vertical
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacement entre champs
            ) {
                // Champ pour le titre
                OutlinedTextField(
                    value = titre,
                    onValueChange = { titre = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Champ pour la description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Champ pour le prix avec validation du format numérique
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

                // Champ pour les caractéristiques
                OutlinedTextField(
                    value = caracteristiques,
                    onValueChange = { caracteristiques = it },
                    label = { Text("Caractéristiques") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Champ pour la localisation
                OutlinedTextField(
                    value = localisation,
                    onValueChange = { localisation = it },
                    label = { Text("Adresse") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Bouton d'enregistrement des modifications
                Button(
                    onClick = {
                        // Conversion du prix en Float avec valeur par défaut
                        val prixFloat: Any = prix.toFloatOrNull() ?: 0f

                        // Création de la map contenant les champs à mettre à jour
                        val fieldsToUpdate: Map<String, Any> = mapOf(
                            "titre" to titre,
                            "description" to description,
                            "prix" to prixFloat,
                            "caracteristiques" to caracteristiques,
                            "localisation" to localisation
                        )

                        // Appel à la fonction de mise à jour dans le ViewModel
                        annonceViewModel.updateAnnonce(annonceId, fieldsToUpdate) { success ->
                            if (success) {
                                // Notification de succès et retour à l'écran précédent
                                Toast.makeText(
                                    context,
                                    "Annonce modifiée avec succès",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } else {
                                // Notification d'erreur
                                Toast.makeText(
                                    context,
                                    "Erreur lors de la modification",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF07B42),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Enregistrer")
                }
            }
        }
    }
}