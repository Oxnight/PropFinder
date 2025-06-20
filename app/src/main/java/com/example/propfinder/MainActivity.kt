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

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        Configuration.getInstance().userAgentValue = packageName

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
    val authViewModel: AuthViewModel = viewModel()
    val annonceViewModel: AnnonceViewModel = viewModel()

    val isLoggedIn = authViewModel.isUserLoggedIn()
    val startDestination = if (isLoggedIn) "main" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentification
        composable("login") {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        composable("register") {
            RegisterPage(navController = navController, authViewModel = authViewModel)
        }

        // Écran principal (Template avec navigation interne)
        composable("main") {
            Template(
                navController = navController,
                authViewModel = authViewModel,
                annonceViewModel = annonceViewModel
            )
        }

        // Navigation globale (hors template)
        composable("edit_annonce/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            EditAnnonceScreen(
                annonceId = id,
                navController = navController,
                annonceViewModel = annonceViewModel
            )
        }

        composable("chat_route/{idDiscussion}") { backStackEntry ->
            val idDiscussion = backStackEntry.arguments?.getString("idDiscussion")
            ChatPage(navController = navController, idDiscussion = idDiscussion.toString())
        }

        composable("modifier_profile_route") {
            ModifierProfilePage(navController = navController)
        }

        composable("message_route/{idAnnonce}") { backStackEntry ->
            val idAnnonce = backStackEntry.arguments?.getString("idAnnonce")
            ChatPage(navController = navController, idAnnonce = idAnnonce)
        }
    }
}
