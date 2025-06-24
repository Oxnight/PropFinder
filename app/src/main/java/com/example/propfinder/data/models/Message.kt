package com.example.propfinder.data.models

import com.google.firebase.Timestamp

// Modèle représentant un message échangé dans une discussion entre utilisateurs
data class Message(
    var id: String = "",                             // Identifiant unique du message
    val contenu: String = "",                        // Contenu du message
    val dateEnvoie: Timestamp = Timestamp.now(),     // Date et heure d'envoi du message
    val idDiscussion: String = "",                   // Identifiant de la discussion à laquelle ce message appartient
    val senderId: String? = ""                       // Identifiant de l'expéditeur
)