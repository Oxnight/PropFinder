package com.example.propfinder.data.states

import com.example.propfinder.data.models.Utilisateur

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: Utilisateur? = null,
    val error: String? = null
)