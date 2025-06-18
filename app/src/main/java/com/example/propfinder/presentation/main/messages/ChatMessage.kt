package com.example.propfinder.presentation.main.messages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.runtime.LaunchedEffect
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


@Composable
fun ChatPage(navController: NavController, idDiscussion: String? = null, idAnnonce : String? = null) {

    val authViewModel: AuthViewModel = viewModel()
    val discussionViewModel: DiscussionViewModel = viewModel()
    val annonceViewModel: AnnonceViewModel = viewModel()
    val messageViewModel: MessageViewModel = viewModel()



    val userId = authViewModel.getUserId()
    val userEnFace = remember { mutableStateOf("") }
    val input = remember { mutableStateOf("") }
    val discussionIdState = remember { mutableStateOf(idDiscussion) }





    LaunchedEffect(idAnnonce) {
        // Si on arrive depuis l’annonce :
        if (discussionIdState.value == null && idAnnonce != null && userId != null) {

            // 1. Chercher d’abord une discussion existante
            discussionViewModel.findDiscussion(idAnnonce, userId) { existing ->
                if (existing != null) {
                    // → On la ré-utilise
                    discussionIdState.value = existing.id
                    messageViewModel.loadMessagesForDiscussion(existing.id)
                } else {
                    // 2. Sinon seulement, on la crée
                    discussionViewModel.createDiscussion(
                        idAnnonce = idAnnonce,
                        idUserSend = userId
                    ) { created ->
                        if (created != null) {
                            discussionIdState.value = created.id
                            // Pas besoin de re-charger, le Flow/LiveData réagira
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



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Barre de haut avec bouton retour
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
            ) {
                Text(" Retour", color = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Discussion",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )


        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messageViewModel.messages) { message ->
                val isMe = message.senderId == userId
                val backgroundColor = if (isMe) Color(0xFFF07B42) else Color(0xFF2E2E2E)
                val textColor = if (isMe) Color.Black else Color.White
                val alignment = if (isMe) Alignment.End else Alignment.Start

                Column(
                    modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input.value,
                onValueChange = {input.value = it},
                placeholder = { Text("Écrire...", color = Color.Gray) },
                modifier = Modifier.weight(1f),
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
                        // discussion déjà prête → on insère juste le message
                        val messageToInsert = Message(
                            contenu = input.value,
                            idDiscussion = currentDiscussionId,
                            senderId = authViewModel.getUserId()
                        )
                        messageViewModel.insertMessage(messageToInsert)
                        input.value = ""
                    }
                    else if (input.value.isNotBlank() && currentDiscussionId == null) {
                        // il faut encore créer la discussion (cas théorique : la création a échoué
                        // ou la LaunchedEffect n’a pas encore fini)
                        if (idAnnonce != null && userId != null) {
                            discussionViewModel.createDiscussion(
                                idAnnonce = idAnnonce,
                                idUserSend = userId
                            ) { created ->
                                created?.let {
                                    discussionIdState.value = it.id   // met à jour l’état
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
                }
                ,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42))
            ) {
                Text("→", color = Color.Black)
            }
        }
    }

}