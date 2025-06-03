package com.example.propfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.propfinder.presentation.auth.LoginPage
import com.example.propfinder.presentation.auth.RegisterPage
import com.example.propfinder.presentation.main.Template
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
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) "main" else "login"
    ) {
        composable("login") {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        composable("register") {
            RegisterPage(navController = navController, authViewModel = authViewModel)
        }
        composable("main") {
            Template(authViewModel = authViewModel)
        }
    }
}