package com.example.propfinder.presentation.main.annonce

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.propfinder.data.models.Annonce
import com.example.propfinder.presentation.main.Template
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel

@Composable
fun AnnonceDetail(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel, navController: NavController, titre: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFD9D9D9))
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = titre,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    imageVector = Icons.Default.Info,
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
        AnnonceDetailContent(
            annonceViewModel = annonceViewModel,
            authViewModel = authViewModel,
            navController = navController,
            titre = titre
        )
    }
}

@Composable
fun AnnonceDetailContent(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel, navController: NavController, titre: String) {
    var annonce by remember { androidx.compose.runtime.mutableStateOf<Annonce?>(null) }

    LaunchedEffect(titre) {
        annonceViewModel.getAllByTitre(titre) { annonces ->
            if (annonces.isNotEmpty()) {
                annonce = annonces.first() // On suppose qu'il y a une seule annonce avec ce titre
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (annonce != null) {
            Column {
                if (annonce?.images?.isNotEmpty() == true) {
                    Text("Images :", style = MaterialTheme.typography.bodyMedium)
                    annonce?.images?.forEach { imageUrl ->
                        Text(imageUrl, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Text("Aucune image disponible", style = MaterialTheme.typography.bodyMedium)
                }
                Text("Type : ${annonce?.type}", style = MaterialTheme.typography.bodyMedium)
                Text("Prix : ${annonce?.prix} €", style = MaterialTheme.typography.bodyMedium)
                Text("Date de publication: ${annonce?.date}", style = MaterialTheme.typography.bodyMedium)
                Text("Posté par : ${annonce?.idUser}", style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = {
                        // CONTACTER l'utilisateur
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Envoyer un message")
                }
                Text("Description : ${annonce?.description}", style = MaterialTheme.typography.bodyMedium)
                Text("Caractéristiques : ${annonce?.caracteristiques}", style = MaterialTheme.typography.bodyMedium)
                Text("Adresse : ${annonce?.localisation}", style = MaterialTheme.typography.bodyMedium)
                Text("Coordonnées : ${annonce?.coordonees}", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text("Chargement des données...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}