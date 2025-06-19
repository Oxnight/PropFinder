package com.example.propfinder.presentation.main.annonce

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.propfinder.R
import com.example.propfinder.data.models.Annonce
import com.example.propfinder.presentation.main.Template
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                    painter = painterResource(id = R.drawable.building),
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
                annonce = annonces.first()
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.appartement1),
                            contentDescription = "Image par défaut",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(160.dp)
                                .width(250.dp)
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Column {
                            Image(
                                painter = painterResource(id = R.drawable.appartement1),
                                contentDescription = "Image par défaut",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Image(
                                painter = painterResource(id = R.drawable.appartement1),
                                contentDescription = "Image par défaut",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                        }
                    }
                }

                Row {
                    Column {

                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFFF07B42))
                            Spacer(modifier = Modifier.width(1.dp))
                            Text(text = "  ${annonce?.prix} €",
                                color = Color(0xFFF07B42),
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Type d'annonce",
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF1E1E1E)
                            )
                            Text(
                                "${annonce?.localisation}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                                maxLines = 2, // ou plus selon le besoin
                                softWrap = true,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .widthIn(max = 200.dp) // largeur max à ajuster selon la maquette
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier.padding(top = 8.dp),
                        onClick = {
                            navController.navigate("message_route/${annonce?.id}")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF07B42) // orange
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Contacter l'utilisateur",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }


                }

                Row {
                    Text("${annonce?.titre}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text("   (${annonce?.type})", style = MaterialTheme.typography.bodyMedium)
                }

                Text("${annonce?.description}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))

                Text("Informations et caractéristiques :",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text("${annonce?.caracteristiques}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))

                var userName by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
                annonceViewModel.getUserNameById(annonce?.idUser ?: "") { name ->
                    userName = name
                }

                val timestamp = annonce?.date ?: System.currentTimeMillis()
                val date = Date(timestamp)
                val formatter = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault())
                val formattedDate = formatter.format(date)

                Text("Posté par ${userName}, le ${formattedDate}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                    ),
                )

                //Text("Coordonnées : ${annonce?.coordonees}", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text("Chargement des données...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}