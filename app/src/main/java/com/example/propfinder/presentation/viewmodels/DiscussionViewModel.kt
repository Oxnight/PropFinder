package com.example.propfinder.presentation.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.propfinder.data.models.Discussion
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration


class DiscussionViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val discussionCollection = firestore.collection("Discussion")

    val discussions = mutableStateListOf<Discussion>()

    private var listenerRegistration: ListenerRegistration? = null

    fun loadDiscussionsForUser(userId: String) {
        // Récupérer les ID des annonces de l'utilisateur
        firestore.collection("Annonce").get().addOnSuccessListener { annonceResult ->
            val userAnnonceList = mutableListOf<String>()
            for (doc in annonceResult) {
                val annonceUserId = doc.getString("idUser")
                if (annonceUserId == userId) {
                    userAnnonceList.add(doc.id)
                }
            }

            // Charger les discussions existantes
            discussionCollection
                .get()
                .addOnSuccessListener { result ->
                    val filteredDiscussions = result.documents.mapNotNull { doc ->
                        val idUserSend = doc.getString("idUserSend") ?: return@mapNotNull null
                        val idAnnonce = doc.getString("idAnnonce") ?: return@mapNotNull null

                        val isSender = idUserSend == userId
                        val isReceiver = idAnnonce in userAnnonceList

                        if (isSender || isReceiver) {
                            Discussion(
                                id = doc.id,
                                idUserSend = idUserSend,
                                idAnnonce = idAnnonce,
                                date = doc.getTimestamp("date") ?: Timestamp.now(),
                            )
                        } else null
                    }.sortedByDescending { it.date }

                    discussions.clear()
                    discussions.addAll(filteredDiscussions)
                }

            // Écoute temps réel des nouvelles discussions
            listenerRegistration = discussionCollection
                .addSnapshotListener { snapshots, error ->
                    if (error != null || snapshots == null) {
                        return@addSnapshotListener
                    }

                    for (change in snapshots.documentChanges) {
                        if (change.type == DocumentChange.Type.ADDED) {
                            val doc = change.document
                            val idUserSend = doc.getString("idUserSend") ?: continue
                            val idAnnonce = doc.getString("idAnnonce") ?: continue

                            val isSender = idUserSend == userId
                            val isReceiver = idAnnonce in userAnnonceList

                            if (isSender || isReceiver) {
                                val newDiscussion = Discussion(
                                    id = doc.id,
                                    idUserSend = idUserSend,
                                    idAnnonce = idAnnonce,
                                    date = doc.getTimestamp("date") ?: Timestamp.now(),
                                )

                                val alreadyExists = discussions.any { it.id == newDiscussion.id }
                                if (!alreadyExists) {
                                    discussions.add(newDiscussion)
                                    discussions.sortByDescending { it.date }
                                }
                            }
                        }
                    }
                }
        }
    }

    fun getDiscussionById(id: String, onResult: (Discussion?) -> Unit) {
        discussionCollection.document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val discussion = Discussion(
                        id = document.id,
                        idUserSend = document.getString("idUserSend") ?: "",
                        idAnnonce = document.getString("idAnnonce") ?: "",
                        date = document.getTimestamp("date") ?: Timestamp.now(),
                    )
                    onResult(discussion)
                }
            }
    }
    fun deleteDiscussion(discussionId: String) {
        discussionCollection.document(discussionId)
            .delete()
            .addOnSuccessListener {
                discussions.removeAll { it.id == discussionId }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun createDiscussion(
        idAnnonce: String,
        idUserSend: String,
        onResult: (Discussion?) -> Unit
    )  {
        val newDocRef = discussionCollection.document()

        val discussion = Discussion(
            id = newDocRef.id,
            idUserSend = idUserSend,
            idAnnonce = idAnnonce,
            date = Timestamp.now()
        )

        newDocRef.set(discussion)
            .addOnSuccessListener {
                onResult(discussion)
            }
            .addOnFailureListener {
                // Log ou message d'erreur si besoin
            }
    }

}
