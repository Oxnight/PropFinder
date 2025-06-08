package com.example.propfinder.presentation.main

import ProfilePage
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.example.propfinder.R
import com.example.propfinder.presentation.home.Recherche
import com.example.propfinder.presentation.main.publish.Publish
import com.example.propfinder.presentation.main.map.OsmdroidMapView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.propfinder.presentation.main.messages.DiscussionsPage
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Template(authViewModel: AuthViewModel, navController: NavController) {
    var selectedIndex by remember { mutableStateOf(0) }
    val annonceViewModel: AnnonceViewModel = viewModel()




    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
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
                    IconButton(onClick = { /* Notifications */ }) {
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
            NavigationBar(
                containerColor = Color(0xFFD9D9D9),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E))
            ) {
                listOf(
                    Icons.Filled.Search to "Search",
                    Icons.Filled.LocationOn to "Map",
                    Icons.Filled.Home to "Publish",
                    Icons.Filled.Email to "Messages",
                    Icons.Filled.Person to "Profil"
                ).forEachIndexed { idx, pair ->
                    NavigationBarItem(
                        icon = { Icon(pair.first, contentDescription = pair.second, modifier = Modifier.size(32.dp)) },
                        label = { Text(pair.second) },
                        selected = selectedIndex == idx,
                        onClick = { selectedIndex = idx }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF1E1E1E))
        ) {
            when (selectedIndex) {
                0 -> Recherche(annonceViewModel = annonceViewModel)
                1 -> OsmdroidMapView(annonceViewModel = annonceViewModel)
                2 -> Publish(annonceViewModel = annonceViewModel)
                3 -> DiscussionsPage(modifier = Modifier, navController = navController)
                4 -> ProfilePage(navController= navController)
            }
        }
    }
}