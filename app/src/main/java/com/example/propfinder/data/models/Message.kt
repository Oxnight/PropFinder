package com.example.propfinder.data.models

data class Message(
    val id: String = "",
    val idUserSend: String = "",
    val idAnnonce: String = "",
    val date_envoi: Long = System.currentTimeMillis(),
    val contenu: String = ""
)