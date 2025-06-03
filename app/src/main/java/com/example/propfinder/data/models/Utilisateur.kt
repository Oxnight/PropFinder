package com.example.propfinder.data.models

data class Utilisateur(
    val id: String = "",
    val nom: String = "",
    val prenom: String = "",
    val mail: String = "",
    val role: String = "",
    val mot_de_passe: String = "",
    val avatar: String = ""
)