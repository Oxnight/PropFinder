package com.example.propfinder.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.propfinder.R
import com.example.propfinder.presentation.home.Recherche
import com.example.propfinder.presentation.main.annonce.AnnonceDetail
import com.example.propfinder.presentation.main.map.OsmdroidMapView
import com.example.propfinder.presentation.main.messages.DiscussionsPage
import com.example.propfinder.presentation.main.publish.Publish
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.presentation.main.profile.ProfilePage
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Template(
    navController: NavController,
    authViewModel: AuthViewModel,
    annonceViewModel: AnnonceViewModel
) {
    // Contrôleur de navigation interne (navigation par onglets)
    val internalNavController = rememberNavController()

    // État de l'entrée de navigation actuelle
    val currentEntry by internalNavController.currentBackStackEntryAsState()
    val currentDestination = currentEntry?.destination?.route

    // État pour afficher les détails d'une annonce sélectionnée
    var selectedTitre by remember { mutableStateOf<String?>(null) }

    // Liste des destinations et des icônes associées pour la bottom bar
    val destinations = listOf(
        "search" to Icons.Filled.Search,
        "map" to Icons.Filled.LocationOn,
        "publish" to Icons.Filled.Home,
        "messages" to Icons.Filled.Email,
        "profile" to Icons.Filled.Person
    )

    // Liste des test tags pour chaque bouton (pour les tests UI)
    val testTags = listOf(
        "home_button",
        null,
        "publish_button",
        "discussion_button",
        "profile_button"
    )

    // Scaffold principal : TopAppBar + BottomNavigation + Corps de page
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    // Logo centré dans la barre du haut
                    Box(Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.propfinder),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.Center),
                            tint = Color.Unspecified
                        )
                    }
                },
                actions = {
                    // Bouton de raccourci vers les messages dans la barre du haut
                    IconButton(onClick = {
                        internalNavController.navigate("messages")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(32.dp),
                            tint = Color.DarkGray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD9D9D9)
                )
            )
        },
        bottomBar = {
            // Barre de navigation inférieure avec onglets
            NavigationBar(
                containerColor = Color(0xFFD9D9D9),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E))
            ) {
                destinations.forEachIndexed { idx, (route, icon) ->
                    NavigationBarItem(
                        modifier = testTags[idx]?.let { Modifier.testTag(it) } ?: Modifier,
                        icon = {
                            Icon(icon, contentDescription = route, modifier = Modifier.size(32.dp))
                        },
                        label = { Text(route) },
                        selected = currentDestination == route,
                        onClick = {
                            if (currentDestination != route) {
                                internalNavController.navigate(route) {
                                    launchSingleTop = true
                                }
                            }
                            selectedTitre = null // Réinitialisation de la sélection
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Corps de la page : contenu variable selon la navigation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Système de navigation interne pour les onglets principaux
            NavHost(
                navController = internalNavController,
                startDestination = "search",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E1E1E))
            ) {
                // Page de recherche d'annonces
                composable("search") {
                    Recherche(
                        annonceViewModel = annonceViewModel,
                        onAnnonceClick = { titre -> selectedTitre = titre }
                    )
                }

                // Carte avec points de localisation des annonces
                composable("map") {
                    OsmdroidMapView(
                        annonceViewModel = annonceViewModel,
                        navController = navController,
                        onAnnonceClick = { titre -> selectedTitre = titre }
                    )
                }

                // Formulaire de publication d'une annonce
                composable("publish") {
                    Publish(
                        annonceViewModel = annonceViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }

                // Messagerie (liste des discussions)
                composable("messages") {
                    DiscussionsPage(navController = navController)
                }

                // Profil utilisateur
                composable("profile") {
                    ProfilePage(navController = navController)
                }
            }

            // Affichage de la fiche annonce en sur-impression si un titre a été sélectionné
            if (selectedTitre != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    AnnonceDetail(
                        annonceViewModel = annonceViewModel,
                        authViewModel = authViewModel,
                        navController = navController,
                        titre = selectedTitre!!,
                        onClose = { selectedTitre = null }
                    )
                }
            }
        }
    }
}