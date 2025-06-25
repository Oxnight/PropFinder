package com.example.propfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.propfinder.presentation.auth.LoginPage
import com.example.propfinder.presentation.auth.RegisterPage
import com.example.propfinder.presentation.main.Template
import com.example.propfinder.presentation.main.annonce.EditAnnonceScreen
import com.example.propfinder.presentation.main.messages.ChatPage
import com.example.propfinder.presentation.main.profile.ModifierProfilePage
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.ui.theme.PropFinderTheme
import com.google.firebase.FirebaseApp
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de Firebase pour l'app
        FirebaseApp.initializeApp(this)

        // Active le rendu sans bordure (Edge-to-edge layout)
        enableEdgeToEdge()

        // Configuration d’OSMDroid : important pour éviter les erreurs de tuiles offline
        Configuration.getInstance().userAgentValue = packageName

        // Définition de l’interface utilisateur avec Jetpack Compose
        setContent {
            PropFinderTheme {
                PropFinderApp()
            }
        }
    }
}

@Composable
fun PropFinderApp() {
    val navController = rememberNavController()

    // Initialisation des ViewModels principaux (authentification et annonces)
    val authViewModel: AuthViewModel = viewModel()
    val annonceViewModel: AnnonceViewModel = viewModel()

    // Détermine la destination initiale selon si l'utilisateur est connecté
    val isLoggedIn = authViewModel.isUserLoggedIn()
    val startDestination = if (isLoggedIn) "main" else "login"

    // Définition des routes de navigation de l'application
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Page de connexion
        composable("login") {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }

        // Page d'inscription
        composable("register") {
            RegisterPage(navController = navController, authViewModel = authViewModel)
        }

        // Template principal (navigation par onglets, si l'utilisateur est connecté)
        composable("main") {
            Template(
                navController = navController,
                authViewModel = authViewModel,
                annonceViewModel = annonceViewModel
            )
        }

        // Page d’édition d’une annonce spécifique (via son ID)
        composable("edit_annonce/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            EditAnnonceScreen(
                annonceId = id,
                navController = navController,
                annonceViewModel = annonceViewModel
            )
        }

        // Accès direct à une discussion via son ID
        composable("chat_route/{idDiscussion}") { backStackEntry ->
            val idDiscussion = backStackEntry.arguments?.getString("idDiscussion")
            ChatPage(navController = navController, idDiscussion = idDiscussion.toString())
        }

        // Page de modification du profil utilisateur
        composable("modifier_profile_route") {
            ModifierProfilePage(navController = navController)
        }

        // Accès à la messagerie depuis une annonce (création de discussion si besoin)
        composable("message_route/{idAnnonce}") { backStackEntry ->
            val idAnnonce = backStackEntry.arguments?.getString("idAnnonce")
            ChatPage(navController = navController, idAnnonce = idAnnonce)
        }
    }
}