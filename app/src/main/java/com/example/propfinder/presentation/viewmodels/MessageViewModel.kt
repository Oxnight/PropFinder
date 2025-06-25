package com.example.propfinder.presentation.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.propfinder.data.models.Message
import com.google.firebase.firestore.*

class MessageViewModel : ViewModel() {

    // Référence à la base de données Firestore
    private val firestore = FirebaseFirestore.getInstance()
    private val messageCollection = firestore.collection("Message")

    // Liste observable des messages (utilisée pour affichage en temps réel dans Compose)
    val messages = mutableStateListOf<Message>()

    // Listener pour mises à jour en temps réel de la discussion
    private var listenerRegistration: ListenerRegistration? = null

    /**
     * Charge les messages liés à une discussion donnée (via son ID),
     * et active un listener en temps réel pour détecter les nouveaux messages.
     */
    fun loadMessagesForDiscussion(discussionId: String) {
        // Supprimer tout ancien listener
        listenerRegistration?.remove()
        messages.clear()

        // Écoute en temps réel des messages de cette discussion
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

                        val documentId = doc.id

                        // Vérification : éviter d’ajouter des doublons
                        val alreadyExists = messages.any {
                            it.contenu == newMessage.contenu &&
                                    it.senderId == newMessage.senderId &&
                                    it.dateEnvoie == newMessage.dateEnvoie
                        }

                        if (!alreadyExists) {
                            messages.add(newMessage)
                            messages.sortBy { it.dateEnvoie } // Trie croissant par date d’envoi
                        }
                    }
                }
            }
    }

    /**
     * Insère un message dans Firestore et met à jour le timestamp de la discussion associée.
     */
    fun insertMessage(message: Message) {
        messageCollection
            .add(message)
            .addOnSuccessListener {
                // Met à jour la date de la discussion pour refléter l’activité récente
                firestore
                    .collection("Discussion")
                    .document(message.idDiscussion)
                    .update("date", message.dateEnvoie)
            }
    }

    /**
     * Supprime le listener en temps réel à la destruction du ViewModel.
     */
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}