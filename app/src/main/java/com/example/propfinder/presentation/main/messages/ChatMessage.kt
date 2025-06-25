package com.example.propfinder.presentation.main.messages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.propfinder.data.models.Message
import com.example.propfinder.presentation.viewmodels.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatPage(navController: NavController, idDiscussion: String? = null, idAnnonce: String? = null) {

    // Initialisation des ViewModels utilisés
    val authViewModel: AuthViewModel = viewModel()
    val discussionViewModel: DiscussionViewModel = viewModel()
    val annonceViewModel: AnnonceViewModel = viewModel()
    val messageViewModel: MessageViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    // Accès aux infos utilisateur
    val utilisateur = profileViewModel.utilisateur
    val destinataireName = remember { mutableStateOf("...") }

    // États internes de la page
    val userId = authViewModel.getUserId()
    val userEnFace = remember { mutableStateOf("") }
    val input = remember { mutableStateOf("") }
    val discussionIdState = remember { mutableStateOf(idDiscussion) }

    // Recherche ou création automatique de la discussion si idAnnonce fourni
    LaunchedEffect(idAnnonce) {
        if (discussionIdState.value == null && idAnnonce != null && userId != null) {
            discussionViewModel.findDiscussion(idAnnonce, userId) { existing ->
                if (existing != null) {
                    discussionIdState.value = existing.id
                    messageViewModel.loadMessagesForDiscussion(existing.id)
                } else {
                    discussionViewModel.createDiscussion(
                        idAnnonce = idAnnonce,
                        idUserSend = userId
                    ) { created ->
                        if (created != null) {
                            discussionIdState.value = created.id
                        }
                    }
                }
            }
        }
    }

    // Chargement des messages à chaque changement d’ID de discussion
    LaunchedEffect(discussionIdState.value) {
        val id = discussionIdState.value
        if (id != null) {
            messageViewModel.loadMessagesForDiscussion(id)
        }
    }

    // Récupération du nom du destinataire pour l’en-tête
    LaunchedEffect(discussionIdState.value) {
        val id = discussionIdState.value
        if (id != null) {
            messageViewModel.loadMessagesForDiscussion(id)
            discussionViewModel.getDiscussionById(id) { discussion ->
                discussion?.let {
                    val myId = authViewModel.getUserId()
                    if (it.idUserSend == myId) {
                        // Si l'utilisateur est l'expéditeur, on récupère le propriétaire de l'annonce
                        annonceViewModel.getUserIdById(it.idAnnonce) { ownerId ->
                            if (ownerId != null) {
                                authViewModel.getUserNameById(ownerId) { name ->
                                    destinataireName.value = name ?: "Destinataire inconnu"
                                }
                            }
                        }
                    } else {
                        // Sinon, on récupère directement le nom de l’expéditeur
                        authViewModel.getUserNameById(it.idUserSend) { name ->
                            destinataireName.value = name ?: "Destinataire inconnu"
                        }
                    }
                }
            }
        }
    }

    // Conteneur principal de la page
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)), // Fond sombre global
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // En-tête avec nom du destinataire et bouton retour
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFD9D9D9))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .height(42.dp)
                    .testTag("back_button")
            ) {
                Text(
                    text = "Retour",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            if (utilisateur != null) {
                // Affichage du nom du destinataire
                Text(
                    text = destinataireName.value,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Indicateur de chargement si profil non encore chargé
                CircularProgressIndicator(
                    color = Color.Gray,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.Center)
                )
            }
        }

        // Liste des messages
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Affichage de chaque message avec style différent selon l'expéditeur
                items(messageViewModel.messages) { message ->
                    val isMe = message.senderId == userId
                    val backgroundColor = if (isMe) Color(0xFFF07B42) else Color(0xFF2E2E2E)
                    val textColor = if (isMe) Color.Black else Color.White
                    val alignment = if (isMe) Alignment.End else Alignment.Start

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = alignment
                    ) {
                        Box(
                            modifier = Modifier
                                .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = message.contenu,
                                color = textColor
                            )
                        }
                    }
                }
            }

            // Zone de saisie + bouton d’envoi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = input.value,
                    onValueChange = { input.value = it },
                    placeholder = { Text("Écrire...", color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("message_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val currentDiscussionId = discussionIdState.value

                        if (input.value.isNotBlank() && currentDiscussionId != null) {
                            // Envoi d’un message dans discussion existante
                            val messageToInsert = Message(
                                contenu = input.value,
                                idDiscussion = currentDiscussionId,
                                senderId = authViewModel.getUserId()
                            )
                            messageViewModel.insertMessage(messageToInsert)
                            input.value = ""
                        } else if (input.value.isNotBlank() && currentDiscussionId == null) {
                            // Création d’une discussion avant d’envoyer le premier message
                            if (idAnnonce != null && userId != null) {
                                discussionViewModel.createDiscussion(
                                    idAnnonce = idAnnonce,
                                    idUserSend = userId
                                ) { created ->
                                    created?.let {
                                        discussionIdState.value = it.id
                                        val message = Message(
                                            contenu = input.value,
                                            idDiscussion = it.id,
                                            senderId = authViewModel.getUserId()
                                        )
                                        messageViewModel.insertMessage(message)
                                        input.value = ""
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.testTag("send_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Envoyer",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}