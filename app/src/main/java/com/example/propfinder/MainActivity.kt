package com.example.propfinder

import ProfilePage
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
import com.example.propfinder.presentation.main.messages.ChatPage
import com.example.propfinder.presentation.main.messages.DiscussionsPage
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.ui.theme.PropFinderTheme
import org.osmdroid.config.Configuration
import com.google.firebase.FirebaseApp


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
    val isLoggedIn = authViewModel.isUserLoggedIn()
    val startDestination = if (isLoggedIn) "main" else "login"



    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        composable("register") {
            RegisterPage(navController = navController, authViewModel = authViewModel)
        }
        composable("main") {
            Template(authViewModel = authViewModel, navController)
        }
        composable("discussion_route") {
            DiscussionsPage(navController = navController)
        }
        composable("chat_route/{idDiscussion}") { backStackEntry ->
            val idDiscussion = backStackEntry.arguments?.getString("idDiscussion")
            ChatPage(navController = navController, idDiscussion = idDiscussion.toString()) //changer
        }
        composable("profile_route") {
            ProfilePage(navController= navController)
        }
    }
}