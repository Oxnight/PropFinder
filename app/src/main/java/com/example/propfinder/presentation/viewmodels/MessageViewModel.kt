package com.example.propfinder.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.propfinder.data.models.Discussion
import com.google.firebase.firestore.FirebaseFirestore
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.data.models.Message
import com.google.android.play.integrity.internal.a
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration


class MessageViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val messageCollection = firestore.collection("Message")

    val messages = mutableStateListOf<Message>()

    private var listenerRegistration: ListenerRegistration? = null


    fun loadMessagesForDiscussion(discussionId: String) {
        listenerRegistration?.remove()
        messages.clear()

        listenerRegistration = messageCollection
            .whereEqualTo("idDiscussion", discussionId)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    return@addSnapshotListener
                }
                for (change in snapshots.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        val doc = change.document
                        val newMessage = doc.toObject(Message::class.java)

                        // Utiliser l'ID automatique du document Firebase
                        val documentId = doc.id

                        // Vérifier si le message n'existe pas déjà en comparant les IDs de document
                        val alreadyExists = messages.any {
                            // Tu peux comparer par contenu + senderId + dateEnvoie si pas d'ID dans le modèle
                            it.contenu == newMessage.contenu &&
                                    it.senderId == newMessage.senderId &&
                                    it.dateEnvoie == newMessage.dateEnvoie
                        }

                        if (!alreadyExists) {
                            messages.add(newMessage)
                            messages.sortBy { it.dateEnvoie }
                        }
                    }
                }
            }
    }

    fun insertMessage(message: Message) {
        messageCollection
            .add(message)
            .addOnSuccessListener {
                FirebaseFirestore.getInstance()
                    .collection("Discussion")
                    .document(message.idDiscussion)
                    .update("date", message.dateEnvoie)
            }
    }


    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}