package com.example.propfinder.presentation.main.messages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.propfinder.R
import com.example.propfinder.data.models.Discussion
import com.example.propfinder.presentation.viewmodels.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OneDiscussion(discussion: Discussion, navController: NavController) {
    // Initialisation des ViewModels nécessaires
    val annonceViewModel: AnnonceViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val discussionViewModel: DiscussionViewModel = viewModel()

    // Formatage de la date de la discussion
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH)
    val formattedDate = formatter.format(discussion.date.toDate())

    // États locaux pour stocker le titre de l'annonce et le nom du destinataire
    val titreAnnonceState = remember { mutableStateOf("") }
    val roleState = remember { mutableStateOf("") }

    val currentUserId = authViewModel.getUserId()

    // État d'affichage du dialogue de suppression
    var showDialog by remember { mutableStateOf(false) }

    // Boîte de dialogue de confirmation pour la suppression d'une discussion
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Supprimer la discussion") },
            text = { Text("Êtes-vous sûr de vouloir supprimer cette discussion ? Cette action est irréversible.") },
            confirmButton = {
                TextButton(onClick = {
                    discussionViewModel.deleteDiscussion(discussion.id)
                    showDialog = false
                }) {
                    Text("Supprimer", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Récupération du titre de l'annonce liée à la discussion
    LaunchedEffect(discussion.idAnnonce) {
        annonceViewModel.getAnnonceTitleById(discussion.idAnnonce) { titre ->
            titreAnnonceState.value = titre ?: "Titre inconnu"
        }
    }

    // Récupération du nom de l'autre utilisateur selon qui est l'émetteur
    LaunchedEffect(discussion.id) {
        if (discussion.idUserSend == currentUserId) {
            annonceViewModel.getUserIdById(discussion.idAnnonce) { idUser ->
                if (idUser != null) {
                    authViewModel.getUserNameById(idUser) { nomPrenom ->
                        roleState.value = nomPrenom ?: "Nom inconnu"
                    }
                }
            }
        } else {
            authViewModel.getUserNameById(discussion.idUserSend) { nomPrenom ->
                roleState.value = nomPrenom ?: "Nom inconnu"
            }
        }
    }

    // Affichage de l'élément de discussion
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable {
                navController.navigate("chat_route/${discussion.id}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar par défaut
        Image(
            painter = painterResource(id = R.drawable.avatar1),
            contentDescription = "Avatar utilisateur",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Informations sur l'annonce et l'utilisateur
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titreAnnonceState.value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = roleState.value,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Bouton pour supprimer la discussion
        IconButton(onClick = { showDialog = true }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Supprimer",
                tint = Color(0xFFB00020),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscussionsPage(modifier: Modifier = Modifier, navController: NavController) {
    val discussionViewModel: DiscussionViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val userId = authViewModel.getUserId()

    // Chargement des discussions associées à l'utilisateur connecté
    LaunchedEffect(userId) {
        discussionViewModel.loadDiscussionsForUser(userId.toString())
    }

    // Conteneur principal de la page
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .background(Color(0xFF1E1E1E))
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // En-tête avec icône et titre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Discussions",
                    tint = Color(0xFFF07B42),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mes discussions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF07B42)
                )
            }

            // Liste des discussions sous forme de cartes
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(discussionViewModel.discussions) { discussion ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            OneDiscussion(discussion, navController)
                        }
                    }
                }
            }
        }
    }
}