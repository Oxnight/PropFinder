package com.example.propfinder.presentation.main.messages

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.presentation.viewmodels.DiscussionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OneDiscussion(discussion: Discussion, navController: NavController) {
    val annonceViewModel: AnnonceViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val discussionViewModel: DiscussionViewModel = viewModel()

    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH)
    val formattedDate = formatter.format(discussion.date.toDate())

    val titreAnnonceState = remember { mutableStateOf("") }
    val roleState = remember { mutableStateOf("") }

    val currentUserId = authViewModel.getUserId()

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Supprimer la discussion") },
            text = { Text("Êtes-vous sûr de vouloir supprimer cette discussion ? Cette action est irréversible.") },            confirmButton = {
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

    LaunchedEffect(discussion.idAnnonce) {
        annonceViewModel.getAnnonceTitleById(discussion.idAnnonce) { titre ->
            titreAnnonceState.value = titre ?: "Titre inconnu"
        }
    }

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

    LaunchedEffect(userId) {
        discussionViewModel.loadDiscussionsForUser(userId.toString())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .background(Color(0xFF1E1E1E))
                .fillMaxSize()
                .padding(16.dp)
        ) {
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