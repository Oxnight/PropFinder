package com.example.propfinder.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.propfinder.data.states.AnnonceState
import com.example.propfinder.data.models.Annonce
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnonceViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val annonceCollection = firestore.collection("Annonce")


    fun getAnnonceTitleById(idAnnonce : String, onResult: (String ?) -> Unit) {

        annonceCollection.document(idAnnonce).get().addOnSuccessListener { document ->
            val titre = document.getString("titre");
            onResult(titre);
        }
    }

    fun getUserIdById(idAnnonce: String,onResult: (String?) -> Unit) {
        annonceCollection.document(idAnnonce).get().addOnSuccessListener { document ->
            val idUser = document.getString("idUser");
            onResult(idUser);
        }
    }



}