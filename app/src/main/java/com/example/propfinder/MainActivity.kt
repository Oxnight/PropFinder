package com.example.propfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.propfinder.ui.theme.PropFinderTheme
import org.osmdroid.config.Configuration
import com.google.firebase.FirebaseApp
import com.example.propfinder.presentation.main.Template

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        Configuration.getInstance().userAgentValue = packageName
        setContent {
            PropFinderTheme {
                Template()
            }
        }
    }
}