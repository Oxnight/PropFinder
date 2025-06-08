package com.example.propfinder.data.models

import com.google.firebase.Timestamp


data class Message(
    var id : String = "",
    val contenu: String = "",
    val dateEnvoie: Timestamp = Timestamp.now(),
    val idDiscussion: String = "",
    val senderId: String ?= ""
)
