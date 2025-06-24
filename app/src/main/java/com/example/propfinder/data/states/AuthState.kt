package com.example.propfinder.data.states

import com.example.propfinder.data.models.Utilisateur

// État représentant les informations liées à l'authentification de l'utilisateur
data class AuthState(
    val isLoading: Boolean = false,             // Indique si une opération de connexion ou vérification est en cours
    val isLoggedIn: Boolean = false,            // Indique si l'utilisateur est actuellement connecté
    val currentUser: Utilisateur? = null,       // Objet utilisateur connecté
    val error: String? = null                   // Message d'erreur éventuel lié à l'authentification
)