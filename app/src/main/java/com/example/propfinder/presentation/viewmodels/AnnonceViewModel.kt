package com.example.propfinder.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.propfinder.data.models.Annonce
import com.google.firebase.firestore.FirebaseFirestore

class AnnonceViewModel : ViewModel() {

    // Initialisation de la base Firestore
    private val firestore = FirebaseFirestore.getInstance()
    private val annonceCollection = firestore.collection("Annonce")

    /**
     * Récupère une annonce à partir de son ID.
     * Le résultat est retourné sous forme de Map de champs.
     */
    fun getAnnonceById(idAnnonce: String, onResult: (Map<String, Any?>?) -> Unit) {
        annonceCollection.document(idAnnonce).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.data)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                println("Erreur lors de la récupération de l'annonce : ${it.message}")
                onResult(null)
            }
    }

    /**
     * Récupère le nom complet (nom + prénom) d’un utilisateur à partir de son ID.
     */
    fun getUserNameById(idUser: String, onResult: (String?) -> Unit) {
        val userCollection = firestore.collection("Utilisateur")
        userCollection.document(idUser).get().addOnSuccessListener { document ->
            val nom = document.getString("nom")
            val prenom = document.getString("prenom")
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

    /**
     * Récupère uniquement le titre d’une annonce selon son ID.
     */
    fun getAnnonceTitleById(idAnnonce: String, onResult: (String?) -> Unit) {
        annonceCollection.document(idAnnonce).get().addOnSuccessListener { document ->
            val titre = document.getString("titre")
            onResult(titre)
        }
    }

    /**
     * Récupère toutes les coordonnées (latitude,longitude) de toutes les annonces.
     */
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

    /**
     * Récupère la localisation à partir de coordonnées géographiques.
     */
    fun getLocalisationByCoordonnees(coordonnees: String, onResult: (String?) -> Unit) {
        annonceCollection.whereEqualTo("coordonees", coordonnees).get()
            .addOnSuccessListener { querySnapshot ->
                val localisation = querySnapshot.documents.firstOrNull()?.getString("localisation")
                onResult(localisation)
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération de la localisation : ${exception.message}")
                onResult(null)
            }
    }

    /**
     * Récupère le titre à partir de coordonnées géographiques.
     */
    fun getTitleByCoordonnees(coordonnees: String, onResult: (String?) -> Unit) {
        annonceCollection.whereEqualTo("coordonees", coordonnees).get()
            .addOnSuccessListener { querySnapshot ->
                val titre = querySnapshot.documents.firstOrNull()?.getString("titre")
                onResult(titre)
            }
            .addOnFailureListener { exception ->
                println("Erreur lors de la récupération du titre : ${exception.message}")
                onResult(null)
            }
    }

    /**
     * Récupère l’ID utilisateur associé à une annonce.
     */
    fun getUserIdById(idAnnonce: String, onResult: (String?) -> Unit) {
        annonceCollection.document(idAnnonce).get().addOnSuccessListener { document ->
            val idUser = document.getString("idUser")
            onResult(idUser)
        }
    }

    /**
     * Publie une nouvelle annonce dans Firestore.
     * Seule la première image est utilisée (si présente).
     */
    fun publishAnnonce(
        titre: String,
        type: String,
        description: String,
        caracteristiques: String,
        localisation: String,
        coordonnees: String,
        prix: Float,
        idUser: String,
        imageUri: Uri?,
        onResult: (Boolean) -> Unit
    ) {
        val annonce = Annonce(
            titre = titre,
            type = type,
            description = description,
            caracteristiques = caracteristiques,
            localisation = localisation,
            coordonees = coordonnees,
            prix = prix,
            idUser = idUser
        )

        firestore.collection("Annonce")
            .add(annonce)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot ajouté avec ID : ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Erreur lors de l'ajout du document : $e")
            }

        // Le résultat de succès est toujours retourné immédiatement (attention : peut être optimisé)
        return onResult(true)
    }

    /**
     * Recherche toutes les annonces correspondant exactement à un titre.
     */
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

    /**
     * Récupère toutes les annonces de la base Firestore.
     */
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

    /**
     * Met à jour certains champs d’une annonce existante.
     */
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

    /**
     * Supprime une annonce à partir de son ID.
     */
    fun deleteAnnonce(idAnnonce: String, onResult: (Boolean) -> Unit) {
        annonceCollection.document(idAnnonce)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}