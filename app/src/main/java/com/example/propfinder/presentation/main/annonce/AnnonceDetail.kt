package com.example.propfinder.presentation.main.annonce

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.testTag

@Composable
fun AnnonceDetail(annonceViewModel: AnnonceViewModel, authViewModel: AuthViewModel, navController: NavController, titre: String) {
    val currentUserId = authViewModel.getUserId()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFD9D9D9))
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        val popped = navController.navigate("main")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                        .size(40.dp)
                        .background(Color(0xFFF07B42), shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = titre,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    painter = painterResource(id = R.drawable.building),
                    contentDescription = "Home",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(42.dp)
                )

            }
            HorizontalDivider(
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
                titre = titre,
            )
        }
    }
}
@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun AnnonceDetailContent(
        annonceViewModel: AnnonceViewModel,
        authViewModel: AuthViewModel,
        navController: NavController,
        titre: String
    ) {
        var showDialog by remember { mutableStateOf(false) }
        val currentUserId = authViewModel.getUserId()
        var annonce by remember { androidx.compose.runtime.mutableStateOf<Annonce?>(null) }
        val context = LocalContext.current

        LaunchedEffect(titre) {
            annonceViewModel.getAllByTitre(titre) { annonces ->
                if (annonces.isNotEmpty()) {
                    annonce = annonces.first()
                }
            }
        }

    CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (annonce != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
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

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = Color(0xFFF07B42)
                                )
                                Spacer(modifier = Modifier.width(1.dp))
                                Text(
                                    text = "${annonce?.prix} €",
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
                        if (currentUserId == annonce?.idUser) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Supprimer l'annonce",
                                modifier = Modifier
                                    .size(52.dp)
                                    .padding(end = 8.dp)
                                    .clickable {
                                        showDialog = true

                                    },
                                tint = Color(0xFFD32F2F) // rouge
                            )
                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = { Text("Supprimer l'annonce") },
                                    text = { Text("Es-tu sûr de vouloir supprimer cette annonce ? Cette action est irréversible.") },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                showDialog = false
                                                annonceViewModel.deleteAnnonce(annonce!!.id!!) { success ->
                                                    if (success) {
                                                        Toast.makeText(context, "Annonce supprimée", Toast.LENGTH_SHORT).show()
                                                        navController.popBackStack()
                                                    } else {
                                                        Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        ) {
                                            Text("Oui", color = Color.Red)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDialog = false }) {
                                            Text("Annuler")
                                        }
                                    }
                                )
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.settings),
                                contentDescription = "Modifier l'annonce",
                                modifier = Modifier
                                    .size(52.dp)
                                    .padding(end = 16.dp)
                                    .clickable {
                                        navController.navigate("edit_annonce/${annonce?.id}")
                                    },
                                tint = Color(0xFF1E1E1E)
                            )
                        } else {
                            Button(
                                onClick = {
                                    navController.navigate("message_route/${annonce?.id}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42)),
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .testTag("contact_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Contacter l'utilisateur",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                        }



                    }

                    Row(modifier = Modifier.padding(bottom = 6.dp)) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = when (annonce?.type) {
                                        "à louer" -> Color(0xFF2196F3)  // Bleu
                                        "à vendre" -> Color(0xFFF44336) // Rouge
                                        else -> Color.Gray
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = when (annonce?.type) {
                                    "à louer" -> "À louer"
                                    "à vendre" -> "À vendre"
                                    else -> "Annonce"
                                },
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Text(
                        "${annonce?.titre}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )


                    Text(
                        "${annonce?.description}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        "Informations et caractéristiques :",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "${annonce?.caracteristiques}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    var userName by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
                    annonceViewModel.getUserNameById(annonce?.idUser ?: "") { name ->
                        userName = name
                    }

                    val timestamp = annonce?.date ?: System.currentTimeMillis()
                    val date = Date(timestamp)
                    val formatter = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault())
                    val formattedDate = formatter.format(date)

                    Text(
                        "Posté par ${userName}, le ${formattedDate}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Light
                        ),
                    )

                }
            } else {
                Text("Chargement des données...", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
        }