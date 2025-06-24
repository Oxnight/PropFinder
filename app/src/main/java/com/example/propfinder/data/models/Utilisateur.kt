package com.example.propfinder.data.models

// Modèle représentant un utilisateur de l'application
data class Utilisateur(
    val id: String = "",       // Identifiant unique de l'utilisateur
    val nom: String = "",      // Nom de famille de l'utilisateur
    val prenom: String = "",   // Prénom de l'utilisateur
    val mail: String = "",     // Adresse email de l'utilisateur
    val age: String = ""       // Âge de l'utilisateur
)