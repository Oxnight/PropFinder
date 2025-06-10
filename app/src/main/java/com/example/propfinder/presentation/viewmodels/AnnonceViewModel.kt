package com.example.propfinder.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.propfinder.data.states.AnnonceState
import com.example.propfinder.data.models.Annonce
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnonceViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val annonceCollection = firestore.collection("Annonce")


    fun getAnnonceTitleById(idAnnonce : String, onResult: (String ?) -> Unit) {

        annonceCollection.document(idAnnonce).get().addOnSuccessListener { document ->
            val titre = document.getString("titre");
            onResult(titre);
        }
    }

    fun getUserIdById(idAnnonce: String,onResult: (String?) -> Unit) {
        annonceCollection.document(idAnnonce).get().addOnSuccessListener { document ->
            val idUser = document.getString("idUser");
            onResult(idUser);
        }
    }

    fun publishAnnonce(titre: String, type : String, description: String, caracteristiques: String, localisation: String, prix: Float, idUser: String, imageUri: Uri?, onResult: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val annonce = Annonce(
            titre = titre,
            type = type,
            description = description,
            caracteristiques = caracteristiques,
            localisation = localisation,
            prix = prix,
            idUser = idUser,
        )
        db.collection("Annonce")
            .add(annonce)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot ajouté avec ID : ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Erreur lors de l'ajout du document : $e")
            }

    }
}