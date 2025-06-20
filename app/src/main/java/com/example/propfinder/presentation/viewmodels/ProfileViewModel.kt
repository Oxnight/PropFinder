package com.example.propfinder.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.propfinder.data.models.Utilisateur
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("Utilisateur")

    var utilisateur by mutableStateOf<Utilisateur?>(null)
        private set
    init {
        loadUtilisateur()
    }


    private fun loadUtilisateur() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("Utilisateur")
            .document(userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("ProfileViewModel", "Erreur lors de l'écoute Firestore", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = Utilisateur(
                        id = snapshot.getString("id") ?: userId,
                        nom = snapshot.getString("nom") ?: "",
                        prenom = snapshot.getString("prenom") ?: "",
                        mail = snapshot.getString("mail") ?: "",
                        age = snapshot.getString("age") ?: ""
                    )
                    Log.d("ProfileViewModel", "Données utilisateur mises à jour : $user")
                    utilisateur = user
                }
            }
    }


    ///ModifierProfile ViewModel
    fun updateUserProfile(prenom: String, nom: String, age: String, email: String) {
        val userId = auth.currentUser?.uid ?: return

        val updates = mutableMapOf<String, Any>()
        if (prenom.isNotBlank()) updates["prenom"] = prenom
        if (nom.isNotBlank()) updates["nom"] = nom
        if (age.isNotBlank()) updates["age"] = age
        if (email.isNotBlank()) updates["mail"] = email

        if (updates.isNotEmpty()) {
            userCollection.document(userId).update(updates)
                .addOnSuccessListener {
                    Log.d("ProfileViewModel", "Profil mis à jour avec : $updates")
                }
                .addOnFailureListener {
                    Log.e("ProfileViewModel", "Erreur lors de la mise à jour", it)
                }
        }
    }

    fun getUserProfile(): Utilisateur? {
        return utilisateur
    }

}
