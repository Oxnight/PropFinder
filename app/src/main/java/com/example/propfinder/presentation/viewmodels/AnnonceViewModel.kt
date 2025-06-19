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
import kotlin.text.get

class AnnonceViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val annonceCollection = firestore.collection("Annonce")

    fun getAnnonceById(idAnnonce: String, onResult: (Map<String, Any?>?) -> Unit) {
        annonceCollection.document(idAnnonce).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.data)  // ✅ renvoie tous les champs dans un Map
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                println("Erreur lors de la récupération de l'annonce : ${it.message}")
                onResult(null)
            }
    }
    fun getUserNameById(idUser: String, onResult: (String?) -> Unit) {
        val userCollection = firestore.collection("Utilisateur")
        userCollection.document(idUser).get().addOnSuccessListener { document ->
            val nom = document.getString("nom");
            val prenom = document.getString("prenom");
            if (nom != null && prenom != null) {
                onResult("$nom $prenom")
            } else {
                onResult(null)
            }
        }.addOnFailureListener { exception ->
            println("Erreur lors de la récupération du nom d'utilisateur : ${exception.message}")
            onResult(null)
        }
    }


    fun getAnnonceTitleById(idAnnonce : String, onResult: (String ?) -> Unit) {

        annonceCollection.document(idAnnonce).get().addOnSuccessListener { document ->
            val titre = document.getString("titre");
            onResult(titre);
        }
    }

    fun getAllCoordinates(onResult: (List<String>) -> Unit) {
        annonceCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val coordinatesList = querySnapshot.documents.mapNotNull { document ->
                    document.getString("coordonees")
                }
                onResult(coordinatesList)
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération des coordonnées : ${exception.message}")
                onResult(emptyList())
            }
    }

    fun getLocalisationByCoordonnees(coordonnees: String, onResult: (String?) -> Unit) {
        annonceCollection.whereEqualTo("coordonees", coordonnees).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.firstOrNull()
                    val localisation = document?.getString("localisation")
                    onResult(localisation)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération de la localisation : ${exception.message}")
                onResult(null)
            }
    }

    fun getTitleByCoordonnees(coordonnees: String, onResult: (String?) -> Unit) {
        annonceCollection.whereEqualTo("coordonees", coordonnees).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.firstOrNull()
                    val titre = document?.getString("titre")
                    onResult(titre)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération du titre : ${exception.message}")
                onResult(null)
            }
    }

    fun getUserIdById(idAnnonce: String,onResult: (String?) -> Unit) {
        annonceCollection.document(idAnnonce).get().addOnSuccessListener { document ->
            val idUser = document.getString("idUser");
            onResult(idUser);
        }
    }

    fun publishAnnonce(titre: String, type : String, description: String, caracteristiques: String, localisation: String, coordonnees: String, prix: Float, idUser: String, imageUri: Uri?, onResult: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val annonce = Annonce(
            titre = titre,
            type = type,
            description = description,
            caracteristiques = caracteristiques,
            localisation = localisation,
            coordonees = coordonnees,
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
        return onResult(true)
    }

    fun getAllByTitre(titre: String, onResult: (List<Annonce>) -> Unit) {
        annonceCollection.whereEqualTo("titre", titre).get()
            .addOnSuccessListener { querySnapshot ->
                val annonces = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Annonce::class.java)?.copy(id = document.id)
                }

                onResult(annonces)
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération des annonces : ${exception.message}")
                onResult(emptyList())
            }
    }

    fun getAllAnnonces(onResult: (List<Annonce>) -> Unit) {
        annonceCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val annonces = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Annonce::class.java)
                }
                onResult(annonces)
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération des annonces : ${exception.message}")
                onResult(emptyList())
            }
    }

    fun updateAnnonce(idAnnonce: String, updatedFields: Map<String, Any>, onResult: (Boolean) -> Unit) {
        annonceCollection.document(idAnnonce).update(updatedFields)
            .addOnSuccessListener {
                println("Annonce mise à jour avec succès.")
                onResult(true)
            }
            .addOnFailureListener { e ->
                println("Erreur lors de la mise à jour : ${e.message}")
                onResult(false)
            }
    }

    fun deleteAnnonce(idAnnonce: String, onResult: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance().collection("Annonce")
            .document(idAnnonce)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}