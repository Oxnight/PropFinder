package com.example.propfinder.data.models

data class Annonce(
    val id: String = "",
    val idUser: String = "",
    val type: String = "",
    val titre: String = "",
    val prix: Float = 0f,
    val description: String = "",
    val caracteristiques: String = "",
    val date: Long = System.currentTimeMillis(),
    val localisation: String = "",
    val coordonees: String = "",
    val images: List<String> = emptyList(),
    val actif: Boolean = true
)