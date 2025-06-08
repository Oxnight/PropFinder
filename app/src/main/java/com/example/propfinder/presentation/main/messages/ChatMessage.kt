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
import kotlin.math.log


@Composable
fun ChatPage(navController: NavController,idDiscussion : String) {

    val authViewModel: AuthViewModel = viewModel()
    val discussionViewModel: DiscussionViewModel = viewModel()
    val annonceViewModel: AnnonceViewModel = viewModel()
    val messageViewModel: MessageViewModel = viewModel()

    val userId = authViewModel.getUserId()
    val userEnFace = remember { mutableStateOf("") }
    val input = remember { mutableStateOf("") }

    LaunchedEffect(idDiscussion) {
        discussionViewModel.getDiscussionById(idDiscussion) { discussion ->
            if (discussion != null) {
                if (discussion.idUserSend == userId) {
                    // Moi = sender → chercher le proprio de l’annonce
                    annonceViewModel.getUserIdById(discussion.idAnnonce) { idUser ->
                        authViewModel.getUserNameById(idUser ?: "") { nomPrenom ->
                            userEnFace.value = nomPrenom ?: "Nom inconnu"
                        }
                    }
                } else {
                    // Moi = proprio → afficher l’envoyeur
                    authViewModel.getUserNameById(discussion.idUserSend) { nomPrenom ->
                        userEnFace.value = nomPrenom ?: "Nom inconnu"
                    }
                }
            }
        }
    }

    LaunchedEffect(idDiscussion) {
        messageViewModel.loadMessagesForDiscussion(idDiscussion)
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
                    val messageToInsert = Message(
                        contenu = input.value,
                        idDiscussion = idDiscussion,
                        senderId = authViewModel.getUserId()
                    )
                    if (input.value != "") {
                        messageViewModel.insertMessage(messageToInsert)
                        input.value = "";
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42))
            ) {
                Text("→", color = Color.Black)
            }
        }
    }

}