package com.example.propfinder.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.propfinder.data.states.AuthState
import com.example.propfinder.data.models.Utilisateur
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val utilisateurCollection = firestore.collection("Utilisateur")

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

                firestore.collection("Utilisateur")
                    .document(uid!!)
                    .set(userDataSendToFireStore)
                    .addOnSuccessListener {
                        onSuccess()
                    }
            }
    }
    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("AUTH", "Connexion réussie pour $email")

                // → TEST FIRESTORE LECTURE
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                FirebaseFirestore.getInstance().collection("Utilisateur")
                    .document(uid)
                    .get()
                    .addOnSuccessListener {
                        Log.d("FIREBASE", "Lecture document OK : ${it.data}")
                    }
                    .addOnFailureListener {
                        Log.e("FIREBASE", "Échec de lecture Firestore", it)
                    }

                onSuccess()
            }
            .addOnFailureListener {
                Log.e("AUTH", "Échec de connexion Firebase", it)
            }
    }


    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

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

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }


}
