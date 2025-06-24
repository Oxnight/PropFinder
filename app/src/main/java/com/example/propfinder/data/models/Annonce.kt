package com.example.propfinder.data.models

// Modèle de données représentant une annonce immobilière
data class Annonce(
    val id: String = "",                        // Identifiant unique de l'annonce
    val idUser: String = "",                    // Identifiant de l'utilisateur ayant créé l'annonce
    val type: String = "",                      // Type d'annonce (à louer / à vendre)
    val titre: String = "",                     // Titre de l'annonce
    val prix: Float = 0f,                       // Prix associé à l'annonce
    val description: String = "",               // Description détaillée de l'annonce
    val caracteristiques: String = "",          // Caractéristiques spécifiques (ex: nombre de pièces, surface, etc...)
    val date: Long = System.currentTimeMillis(),// Date de création de l'annonce, en timestamp (ms)
    val localisation: String = "",              // Adresse / lieu géographique de l'annonce
    val coordonees: String = "",                // Coordonnées GPS (latitude/longitude) sous forme de chaîne
    val images: List<String> = emptyList(),     // Liste des URLs des images associées à l'annonce
    val actif: Boolean = true                   // Statut de l'annonce
)