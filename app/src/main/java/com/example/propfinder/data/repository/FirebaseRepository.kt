package com.example.propfinder.data.repository

import android.net.Uri
import com.example.propfinder.data.models.Annonce
import com.example.propfinder.data.models.Message
import com.example.propfinder.data.models.Utilisateur
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Authentication
    suspend fun signUp(email: String, password: String, nom: String, prenom: String, age: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")

            // Créer l'utilisateur dans Firestore
            val utilisateur = Utilisateur(
                id = userId,
                nom = nom,
                prenom = prenom,
                mail = email,
                role = "user", // Fixed: role was not defined
                mot_de_passe = "", // Ne pas stocker le mot de passe en clair
                avatar = ""
            )

            firestore.collection("utilisateurs")
                .document(userId)
                .set(utilisateur)
                .await()

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Utilisateur operations
    suspend fun getUtilisateur(userId: String): Result<Utilisateur> {
        return try {
            val document = firestore.collection("utilisateurs")
                .document(userId)
                .get()
                .await()

            val utilisateur = document.toObject(Utilisateur::class.java)
                ?: throw Exception("Utilisateur non trouvé")
            Result.success(utilisateur)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUtilisateur(utilisateur: Utilisateur): Result<Unit> {
        return try {
            firestore.collection("utilisateurs")
                .document(utilisateur.id)
                .set(utilisateur)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Annonce operations
    suspend fun createAnnonce(annonce: Annonce, imageUris: List<Uri>): Result<String> {
        return try {
            val annonceId = UUID.randomUUID().toString()

            // Upload des images
            val imageUrls = mutableListOf<String>()
            imageUris.forEach { uri ->
                val imageRef = storage.reference.child("annonces/$annonceId/${UUID.randomUUID()}")
                val uploadTask = imageRef.putFile(uri).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await()
                imageUrls.add(downloadUrl.toString())
            }

            // Créer l'annonce avec les URLs des images
            val annonceWithImages = annonce.copy(
                id = annonceId,
                images = imageUrls
            )

            firestore.collection("annonces")
                .document(annonceId)
                .set(annonceWithImages)
                .await()

            Result.success(annonceId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnnonces(): Result<List<Annonce>> {
        return try {
            val querySnapshot = firestore.collection("annonces")
                .whereEqualTo("actif", true)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val annonces = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Annonce::class.java)
            }

            Result.success(annonces)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnnoncesByUser(userId: String): Result<List<Annonce>> {
        return try {
            val querySnapshot = firestore.collection("annonces")
                .whereEqualTo("idUser", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val annonces = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Annonce::class.java)
            }

            Result.success(annonces)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Message operations
    suspend fun sendMessage(message: Message): Result<String> {
        return try {
            val messageId = UUID.randomUUID().toString()
            val messageWithId = message.copy(id = messageId)

            firestore.collection("messages")
                .document(messageId)
                .set(messageWithId)
                .await()

            Result.success(messageId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessagesForUser(userId: String): Result<List<Message>> {
        return try {
            val querySnapshot = firestore.collection("messages")
                .whereEqualTo("idUserSend", userId)
                .orderBy("date_envoi", Query.Direction.DESCENDING)
                .get()
                .await()

            val messages = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Message::class.java)
            }

            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessagesForAnnonce(annonceId: String): Result<List<Message>> {
        return try {
            val querySnapshot = firestore.collection("messages")
                .whereEqualTo("idAnnonce", annonceId)
                .orderBy("date_envoi", Query.Direction.ASCENDING)
                .get()
                .await()

            val messages = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Message::class.java)
            }

            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}