package com.example.propfinder.data.models

import com.google.firebase.Timestamp

data class Discussion(
    val id: String = "",
    val idUserSend: String = "",     // celui qui a envoyé le premier message
    val idAnnonce: String = "",      // l’annonce concernée
    val date: Timestamp = Timestamp.now(), // date du dernier message

)
