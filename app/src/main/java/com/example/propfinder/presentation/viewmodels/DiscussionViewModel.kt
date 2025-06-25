package com.example.propfinder.presentation.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.propfinder.data.models.Discussion
import com.google.firebase.firestore.*
import com.google.firebase.Timestamp

class DiscussionViewModel : ViewModel() {

    // Référence à Firestore
    private val firestore = FirebaseFirestore.getInstance()
    private val discussionCollection = firestore.collection("Discussion")

    // Liste observable de discussions (utilisée côté UI)
    val discussions = mutableStateListOf<Discussion>()

    // Listener Firestore pour la synchronisation en temps réel
    private var listenerRegistration: ListenerRegistration? = null

    /**
     * Charge toutes les discussions dans lesquelles l’utilisateur est impliqué :
     * - en tant qu’expéditeur (`idUserSend`)
     * - ou en tant que propriétaire d’annonce
     */
    fun loadDiscussionsForUser(userId: String) {
        // Récupération de toutes les annonces publiées par l'utilisateur
        firestore.collection("Annonce").get().addOnSuccessListener { annonceResult ->
            val userAnnonceList = mutableListOf<String>()
            for (doc in annonceResult) {
                val annonceUserId = doc.getString("idUser")
                if (annonceUserId == userId) {
                    userAnnonceList.add(doc.id)
                }
            }

            // Chargement des discussions correspondantes
            discussionCollection.get().addOnSuccessListener { result ->
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
                            date = doc.getTimestamp("date") ?: Timestamp.now()
                        )
                    } else null
                }.sortedByDescending { it.date }

                discussions.clear()
                discussions.addAll(filteredDiscussions)
            }

            // Ajoute un listener pour synchroniser les modifications en temps réel
            listenerRegistration = discussionCollection.addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener

                for (change in snapshots.documentChanges) {
                    val doc = change.document
                    val idUserSend = doc.getString("idUserSend") ?: continue
                    val idAnnonce = doc.getString("idAnnonce") ?: continue
                    val isSender = idUserSend == userId
                    val isReceiver = idAnnonce in userAnnonceList
                    if (!isSender && !isReceiver) continue

                    val updatedDiscussion = Discussion(
                        id = doc.id,
                        idUserSend = idUserSend,
                        idAnnonce = idAnnonce,
                        date = doc.getTimestamp("date") ?: Timestamp.now()
                    )

                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                            if (discussions.none { it.id == updatedDiscussion.id }) {
                                discussions.add(updatedDiscussion)
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val index = discussions.indexOfFirst { it.id == updatedDiscussion.id }
                            if (index != -1) {
                                discussions[index] = updatedDiscussion
                            }
                        }
                        else -> {}
                    }
                }

                // Tri des discussions par date (plus récentes en haut)
                discussions.sortByDescending { it.date }
            }
        }
    }

    /**
     * Récupère une discussion par son ID.
     */
    fun getDiscussionById(id: String, onResult: (Discussion?) -> Unit) {
        discussionCollection.document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val discussion = Discussion(
                        id = document.id,
                        idUserSend = document.getString("idUserSend") ?: "",
                        idAnnonce = document.getString("idAnnonce") ?: "",
                        date = document.getTimestamp("date") ?: Timestamp.now()
                    )
                    onResult(discussion)
                }
            }
    }

    /**
     * Supprime une discussion de Firestore et de la liste observable.
     */
    fun deleteDiscussion(discussionId: String) {
        discussionCollection.document(discussionId)
            .delete()
            .addOnSuccessListener {
                discussions.removeAll { it.id == discussionId }
            }
    }

    /**
     * Supprime le listener temps réel lors de la destruction du ViewModel.
     */
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    /**
     * Crée une nouvelle discussion et la stocke dans Firestore.
     */
    fun createDiscussion(
        idAnnonce: String,
        idUserSend: String,
        onResult: (Discussion?) -> Unit
    ) {
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
            }
    }

    /**
     * Vérifie si une discussion entre un utilisateur et une annonce existe déjà.
     */
    fun findDiscussion(
        idAnnonce: String,
        idUserSend: String,
        onResult: (Discussion?) -> Unit
    ) {
        discussionCollection
            .whereEqualTo("idAnnonce", idAnnonce)
            .whereEqualTo("idUserSend", idUserSend)
            .limit(1)
            .get()
            .addOnSuccessListener { snap ->
                onResult(snap.documents.firstOrNull()?.toObject(Discussion::class.java))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}