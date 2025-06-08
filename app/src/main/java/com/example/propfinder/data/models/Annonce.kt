package com.example.propfinder.data.models

data class Annonce(
    val idUser: String = "",
    val type: String = "", // "à vendre" ou "à louer"
    val titre: String = "",
    val prix: Float = 0f,
    val description: String = "",
    val caracteristiques: String = "",
    val date: Long = System.currentTimeMillis(),
    val localisation: String = "",
    val images: List<String> = emptyList(),
    val actif: Boolean = true
)