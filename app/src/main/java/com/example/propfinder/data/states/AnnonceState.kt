package com.example.propfinder.data.states

import com.example.propfinder.data.models.Annonce

// État utilisé pour gérer l'affichage et les interactions avec la liste d'annonces dans l'UI
data class AnnonceState(
    val annonces: List<Annonce> = emptyList(),   // Liste des annonces actuellement chargées
    val isLoading: Boolean = false,              // Indique si les données sont en cours de chargement
    val error: String? = null,                   // Message d'erreur éventuel
    val isPublishing: Boolean = false,           // Indique si une annonce est en cours de publication
    val publishSuccess: Boolean = false          // Indique si la dernière publication a été un succès
)