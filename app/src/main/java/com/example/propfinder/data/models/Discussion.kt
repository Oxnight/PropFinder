package com.example.propfinder.data.models

import com.google.firebase.Timestamp

// Modèle représentant une discussion liée à une annonce
data class Discussion(
    val id: String = "",                     // Identifiant unique de la discussion
    val idUserSend: String = "",             // Identifiant de l'utilisateur ayant initié la discussion
    val idAnnonce: String = "",              // Identifiant de l'annonce concernée par la discussion
    val date: Timestamp = Timestamp.now()    // Date de création de la discussion (générée automatiquement)
)