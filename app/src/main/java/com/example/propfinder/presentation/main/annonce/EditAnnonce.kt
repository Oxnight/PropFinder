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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun EditAnnonceScreen(
    annonceId: String,
    navController: NavController,
    annonceViewModel: AnnonceViewModel
) {
    val context = LocalContext.current

    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prix by remember { mutableStateOf("") }
    var caracteristiques by remember { mutableStateOf("") }
    var localisation by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(annonceId) {
        annonceViewModel.getAnnonceById(annonceId) { data ->
            if (data != null) {
                titre = data["titre"]?.toString() ?: ""
                description = data["description"]?.toString() ?: ""
                prix = data["prix"]?.toString() ?: ""
                caracteristiques = data["caracteristiques"]?.toString() ?: ""
                localisation = data["localisation"]?.toString() ?: ""
            }
            isLoading = false
        }
    }

    if (isLoading) {
        Text("Chargement...", modifier = Modifier.padding(16.dp))
        return
    }
    Box(modifier = Modifier
        .background(Color(0xFF1E1E1E))
        .fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFD9D9D9)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "  Modifier l'annonce",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleLarge
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

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = titre,
                    onValueChange = { titre = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

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

                OutlinedTextField(
                    value = caracteristiques,
                    onValueChange = { caracteristiques = it },
                    label = { Text("Caractéristiques") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = localisation,
                    onValueChange = { localisation = it },
                    label = { Text("Adresse") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val prixFloat: Any = prix.toFloatOrNull() ?: 0f
                        val fieldsToUpdate: Map<String, Any> = mapOf(
                            "titre" to titre,
                            "description" to description,
                            "prix" to prixFloat,
                            "caracteristiques" to caracteristiques,
                            "localisation" to localisation
                        )
                        annonceViewModel.updateAnnonce(annonceId, fieldsToUpdate) { success ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Annonce modifiée avec succès",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } else {
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
