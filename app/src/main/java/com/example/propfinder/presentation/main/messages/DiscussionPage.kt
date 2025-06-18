package com.example.propfinder.presentation.main.messages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.example.propfinder.R
import androidx.compose.ui.Modifier
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.propfinder.data.models.Discussion
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.presentation.viewmodels.DiscussionViewModel
import java.text.SimpleDateFormat
import java.util.Locale

//TODO : quand il crée une discussion il doit mettre dans idUserSend l'utilisateur actuel
@Composable
fun OneDiscussion(discussion: Discussion, navController: NavController ) {
    val annonceViewModel : AnnonceViewModel = viewModel();
    val authViewModel : AuthViewModel = viewModel()
    val discussionViewModel: DiscussionViewModel = viewModel()


    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH)
    val formattedDate = formatter.format(discussion.date.toDate())

    val titreAnnonceState = remember { mutableStateOf("") }
    val roleState = remember { mutableStateOf("") }

    val currentUserId = authViewModel.getUserId()

    // 🔹 Charger le titre de l’annonce
    LaunchedEffect(discussion.idAnnonce) {
        annonceViewModel.getAnnonceTitleById(discussion.idAnnonce) { titre ->
            titreAnnonceState.value = titre ?: "Titre inconnu"
        }
    }

    // 🔹 Charger le nom de l’autre utilisateur (sender ou receiver)
    LaunchedEffect(discussion.id) {
        if (discussion.idUserSend == currentUserId) {
            // Moi j’ai envoyé → trouver le propriétaire de l’annonce (le receveur)
            annonceViewModel.getUserIdById(discussion.idAnnonce) { idUser ->
                if (idUser != null) {
                    authViewModel.getUserNameById(idUser) { nomPrenom ->
                        roleState.value = nomPrenom ?: "Nom inconnu"
                    }
                }
            }
        } else {
            // Moi je suis le receveur → afficher l’expéditeur
            authViewModel.getUserNameById(discussion.idUserSend) { nomPrenom ->
                roleState.value = nomPrenom ?: "Nom inconnu"
            }
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(5.dp))
            .padding(10.dp)
            .clickable {
                navController.navigate("chat_route/${discussion.id}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatar1),
            contentDescription = "Avatar utilisateur",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Black, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f) // occupe tout l’espace restant
        ) {
            Text(
                text = titreAnnonceState.value, //Appartemetn 3 pièce
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(
                text = roleState.value,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(text = formattedDate)
        }

        IconButton(onClick = {
            discussionViewModel.deleteDiscussion(discussion.id)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Supprimer",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
    }
}

@Composable
fun DiscussionsPage(modifier: Modifier = Modifier, navController: NavController) {
    val discussionViewModel: DiscussionViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val userId = authViewModel.getUserId()

    LaunchedEffect(userId) {
        discussionViewModel.loadDiscussionsForUser(userId.toString())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .background(Color(0xFFF5F5F5))
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Discussions",
                    modifier = Modifier.size(26.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mes discussions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(discussionViewModel.discussions) { discussion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("chat/${discussion.id}") },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        OneDiscussion(discussion, navController)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { /* Rien pour l’instant */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Créer une discussion"
            )
        }
    }
}


