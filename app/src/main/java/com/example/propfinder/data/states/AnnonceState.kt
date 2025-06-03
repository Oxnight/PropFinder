package com.example.propfinder.data.states

import com.example.propfinder.data.models.Annonce

data class AnnonceState(
    val annonces: List<Annonce> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPublishing: Boolean = false,
    val publishSuccess: Boolean = false
)