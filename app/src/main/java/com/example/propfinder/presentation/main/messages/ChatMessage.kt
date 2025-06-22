package com.example.propfinder.presentation.main.messages

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.TextField
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.propfinder.data.models.Discussion
import com.example.propfinder.data.models.Message
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.presentation.viewmodels.MessageViewModel
import com.example.propfinder.presentation.viewmodels.DiscussionViewModel
import com.example.propfinder.presentation.viewmodels.ProfileViewModel
import kotlin.math.log


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatPage(navController: NavController, idDiscussion: String? = null, idAnnonce : String? = null) {

    val authViewModel: AuthViewModel = viewModel()
    val discussionViewModel: DiscussionViewModel = viewModel()
    val annonceViewModel: AnnonceViewModel = viewModel()
    val messageViewModel: MessageViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val utilisateur = profileViewModel.utilisateur
    val destinataireName = remember { mutableStateOf("...") }

    val userId = authViewModel.getUserId()
    val userEnFace = remember { mutableStateOf("") }
    val input = remember { mutableStateOf("") }
    val discussionIdState = remember { mutableStateOf(idDiscussion) }

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

    LaunchedEffect(discussionIdState.value) {
        val id = discussionIdState.value
        if (id != null) {
            messageViewModel.loadMessagesForDiscussion(id)
        }
    }

    LaunchedEffect(discussionIdState.value) {
        val id = discussionIdState.value
        if (id != null) {
            messageViewModel.loadMessagesForDiscussion(id)

            discussionViewModel.getDiscussionById(id) { discussion ->
                discussion?.let {
                    val myId = authViewModel.getUserId()
                    val idDestinataire = if (it.idUserSend == myId) {
                        annonceViewModel.getUserIdById(it.idAnnonce) { ownerId ->
                            if (ownerId != null) {
                                authViewModel.getUserNameById(ownerId) { name ->
                                    destinataireName.value = name ?: "Destinataire inconnu"
                                }
                            }
                        }
                    } else {
                        authViewModel.getUserNameById(it.idUserSend) { name ->
                            destinataireName.value = name ?: "Destinataire inconnu"
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFD9D9D9))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Bouton retour à gauche
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
                Text(
                    text = destinataireName.value,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                CircularProgressIndicator(
                    color = Color.Gray,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.Center)
                )
            }
        }

        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
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
                            val messageToInsert = Message(
                                contenu = input.value,
                                idDiscussion = currentDiscussionId,
                                senderId = authViewModel.getUserId()
                            )
                            messageViewModel.insertMessage(messageToInsert)
                            input.value = ""
                        } else if (input.value.isNotBlank() && currentDiscussionId == null) {
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