package com.example.propfinder.data.models

import com.google.firebase.Timestamp

data class Discussion(
    val id: String = "",
    val idUserSend: String = "",
    val idAnnonce: String = "",
    val date: Timestamp = Timestamp.now(),

)
