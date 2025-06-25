package com.example.propfinder.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.propfinder.data.models.Utilisateur
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    // Instance Firebase Auth pour l’authentification
    private val auth = FirebaseAuth.getInstance()

    // Instance Firestore pour stocker les données utilisateur
    private val firestore = FirebaseFirestore.getInstance()
    private val utilisateurCollection = firestore.collection("Utilisateur")

    /**
     * Fonction d'inscription d’un nouvel utilisateur avec création d’un compte Firebase Auth
     * et insertion des informations dans Firestore.
     */
    fun signUp(
        email: String,
        password: String,
        nom: String,
        prenom: String,
        age: String,
        onSuccess: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid

                val userDataSendToFireStore = Utilisateur(
                    id = uid.toString(),
                    mail = email,
                    nom = nom,
                    prenom = prenom,
                    age = age
                )

                // Ajout de l'utilisateur dans la collection "Utilisateur"
                firestore.collection("Utilisateur")
                    .document(uid!!) // Utilisation du même UID que dans Firebase Auth
                    .set(userDataSendToFireStore)
                    .addOnSuccessListener {
                        onSuccess()
                    }
            }
    }

    /**
     * Connexion d’un utilisateur avec email et mot de passe.
     * En cas d’échec, le callback `onFailure` est appelé.
     */
    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                Log.e("AuthViewModel", "Échec de connexion", exception)
                onFailure(exception)
            }
    }

    /**
     * Récupère l'ID utilisateur actuellement connecté (UID Firebase).
     */
    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Récupère le nom complet d’un utilisateur à partir de son ID.
     */
    fun getUserNameById(idUser: String, onResult: (String?) -> Unit) {
        utilisateurCollection.document(idUser)
            .get()
            .addOnSuccessListener { document ->
                val nom = document.getString("nom")
                val prenom = document.getString("prenom")
                val nomPrenom = "$prenom $nom"
                onResult(nomPrenom)
            }
    }

    /**
     * Vérifie si un utilisateur est actuellement connecté.
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Déconnecte l'utilisateur actuellement connecté.
     */
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}