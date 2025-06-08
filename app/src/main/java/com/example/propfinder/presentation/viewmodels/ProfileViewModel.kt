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

    var utilisateur by mutableStateOf<Utilisateur?>(null)
        private set
    init {
        loadUtilisateur()
    }


    private fun loadUtilisateur() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("Utilisateur")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val user = Utilisateur(
                    id = doc.getString("id") ?: userId,
                    nom = doc.getString("nom") ?: "",
                    prenom = doc.getString("prenom") ?: "",
                    mail = doc.getString("mail") ?: "",
                    age = doc.getString("age") ?: ""
                )
                Log.d("ProfileViewModel", "Données utilisateur récupérées : $user")
                utilisateur = user
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileViewModel", "Erreur lors du chargement de l'utilisateur", exception)
            }
    }

}
