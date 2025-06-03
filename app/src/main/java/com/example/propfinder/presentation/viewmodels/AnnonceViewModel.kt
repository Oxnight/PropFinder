package com.example.propfinder.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.propfinder.data.repository.FirebaseRepository
import com.example.propfinder.data.states.AnnonceState
import com.example.propfinder.data.models.Annonce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnonceViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _annonceState = MutableStateFlow(AnnonceState())
    val annonceState: StateFlow<AnnonceState> = _annonceState.asStateFlow()

    init {
        loadAnnonces()
    }

    fun loadAnnonces() {
        _annonceState.value = _annonceState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            repository.getAnnonces().fold(
                onSuccess = { annonces ->
                    _annonceState.value = _annonceState.value.copy(
                        annonces = annonces,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _annonceState.value = _annonceState.value.copy(
                        isLoading = false,
                        error = "Erreur lors du chargement des annonces"
                    )
                }
            )
        }
    }

    fun publishAnnonce(
        type: String,
        titre: String,
        prix: Float,
        description: String,
        caracteristiques: String,
        localisation: String,
        imageUris: List<Uri>
    ) {
        val userId = repository.getCurrentUserId()
        if (userId == null) {
            _annonceState.value = _annonceState.value.copy(error = "Vous devez être connecté pour publier une annonce")
            return
        }

        _annonceState.value = _annonceState.value.copy(isPublishing = true, error = null, publishSuccess = false)

        val annonce = Annonce(
            idUser = userId,
            type = type,
            titre = titre,
            prix = prix,
            description = description,
            caracteristiques = caracteristiques,
            localisation = localisation
        )

        viewModelScope.launch {
            repository.createAnnonce(annonce, imageUris).fold(
                onSuccess = { annonceId ->
                    _annonceState.value = _annonceState.value.copy(
                        isPublishing = false,
                        publishSuccess = true
                    )
                    // Recharger les annonces pour inclure la nouvelle
                    loadAnnonces()
                },
                onFailure = { exception ->
                    _annonceState.value = _annonceState.value.copy(
                        isPublishing = false,
                        error = "Erreur lors de la publication de l'annonce"
                    )
                }
            )
        }
    }

    fun getUserAnnonces(userId: String) {
        _annonceState.value = _annonceState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            repository.getAnnoncesByUser(userId).fold(
                onSuccess = { annonces ->
                    _annonceState.value = _annonceState.value.copy(
                        annonces = annonces,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _annonceState.value = _annonceState.value.copy(
                        isLoading = false,
                        error = "Erreur lors du chargement de vos annonces"
                    )
                }
            )
        }
    }

    fun clearError() {
        _annonceState.value = _annonceState.value.copy(error = null)
    }

    fun clearPublishSuccess() {
        _annonceState.value = _annonceState.value.copy(publishSuccess = false)
    }
}