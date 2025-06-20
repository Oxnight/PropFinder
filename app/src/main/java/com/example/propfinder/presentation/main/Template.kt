package com.example.propfinder.presentation.main

import ProfilePage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.propfinder.R
import com.example.propfinder.presentation.home.Recherche
import com.example.propfinder.presentation.main.annonce.AnnonceDetail
import com.example.propfinder.presentation.main.map.OsmdroidMapView
import com.example.propfinder.presentation.main.messages.DiscussionsPage
import com.example.propfinder.presentation.main.publish.Publish
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import androidx.compose.ui.platform.testTag



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Template(
    navController: NavController,
    authViewModel: AuthViewModel,
    annonceViewModel: AnnonceViewModel
) {
    val internalNavController = rememberNavController()
    val currentEntry by internalNavController.currentBackStackEntryAsState()
    val currentDestination = currentEntry?.destination?.route
    var selectedTitre by remember { mutableStateOf<String?>(null) }

    val destinations = listOf(
        "search" to Icons.Filled.Search,
        "map" to Icons.Filled.LocationOn,
        "publish" to Icons.Filled.Home,
        "messages" to Icons.Filled.Email,
        "profile" to Icons.Filled.Person
    )

    val testTags = listOf(
        "home_button",
        null, // pas de test tag pour "map"
        "publish_button",
        "discussion_button",
        "profile_button"
    )

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
            NavigationBar(containerColor = Color(0xFFD9D9D9)) {
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
                            selectedTitre = null
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            NavHost(
                navController = internalNavController,
                startDestination = "search",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E1E1E))
            ) {
                composable("search") {
                    Recherche(
                        annonceViewModel = annonceViewModel,
                        onAnnonceClick = { titre -> selectedTitre = titre }
                    )
                }
                composable("map") {
                    OsmdroidMapView(
                        annonceViewModel = annonceViewModel,
                        navController = navController,
                        onAnnonceClick = { titre -> selectedTitre = titre }
                    )
                }
                composable("publish") {
                    Publish(
                        annonceViewModel = annonceViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }
                composable("messages") {
                    DiscussionsPage(navController = navController)
                }
                composable("profile") {
                    ProfilePage(navController = navController)
                }
            }

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
