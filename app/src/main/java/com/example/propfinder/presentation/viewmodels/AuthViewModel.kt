package com.example.propfinder.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.propfinder.data.repository.FirebaseRepository
import com.example.propfinder.data.states.AuthState
import com.example.propfinder.data.models.Utilisateur
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val userId = repository.getCurrentUserId()
        if (userId != null) {
            viewModelScope.launch {
                repository.getUtilisateur(userId).fold(
                    onSuccess = { user ->
                        _authState.value = _authState.value.copy(
                            isLoggedIn = true,
                            currentUser = user
                        )
                    },
                    onFailure = {
                        signOut()
                    }
                )
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(error = "Veuillez remplir tous les champs")
            return
        }

        _authState.value = _authState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            repository.signIn(email, password).fold(
                onSuccess = { userId ->
                    repository.getUtilisateur(userId).fold(
                        onSuccess = { user ->
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                currentUser = user
                            )
                        },
                        onFailure = { exception ->
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                error = "Erreur lors de la récupération du profil"
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "Email ou mot de passe incorrect"
                    )
                }
            )
        }
    }

    fun signUp(email: String, password: String, nom: String, prenom: String, age: String) {
        if (email.isBlank() || password.isBlank() || nom.isBlank() || prenom.isBlank() || age.isBlank()) {
            _authState.value = _authState.value.copy(error = "Veuillez remplir tous les champs")
            return
        }

        _authState.value = _authState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            repository.signUp(email, password, nom, prenom, age).fold(
                onSuccess = { userId ->
                    repository.getUtilisateur(userId).fold(
                        onSuccess = { user ->
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                currentUser = user
                            )
                        },
                        onFailure = { exception ->
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                error = "Erreur lors de la création du profil"
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "Erreur lors de la création du compte"
                    )
                }
            )
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}