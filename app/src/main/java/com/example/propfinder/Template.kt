package com.example.propfinder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Template() {
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(

        topBar = {
            // TopAppBar avec l'icône centrée
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    Box(modifier = Modifier.fillMaxWidth()) {
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
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications, // Icône de notification de Material Icons
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
                contentColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E))
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search", modifier = Modifier.size(32.dp) ) },
                    label = { Text("Search") },
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Map", modifier = Modifier.size(32.dp) ) },
                    label = { Text("Map") },
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Publish", modifier = Modifier.size(32.dp) ) },
                    label = { Text("Publish") },
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Email, contentDescription = "Messages", modifier = Modifier.size(32.dp) ) },
                    label = { Text("Messages") },
                    selected = selectedIndex == 3,
                    onClick = { selectedIndex = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profil", modifier = Modifier.size(32.dp) ) },
                    label = { Text("Profil") },
                    selected = selectedIndex == 4,
                    onClick = { selectedIndex = 4 }
                )
            }
        }
    )

    { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFF1E1E1E))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                1 -> OsmdroidMapView()
                2 -> Publish()
                0 -> Recherche()
                else -> Text(
                    text = "Page ${when (selectedIndex) {
                        3 -> "Mail"
                        4 -> "Profile"
                        else -> ""
                    }}",
                    color = Color.White
                )
            }
        }
    }
}
