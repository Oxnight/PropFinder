package com.example.propfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.propfinder.ui.theme.PropFinderTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.navigation.compose.*



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Configuration.getInstance().userAgentValue = packageName
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginPage(navController) }
                composable("register") { RegisterPage() }
            }
        }
    }
}

@Composable
fun OsmdroidMapView() {
    AndroidView(
        factory = { context: Context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(16.0)
                controller.setCenter(GeoPoint(47.637959, 6.862894))

                val drawable = resources.getDrawable(R.drawable.pinpoint) as BitmapDrawable
                val bitmap = drawable.bitmap
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 40, 50, false)
                val scaledDrawable = BitmapDrawable(resources, scaledBitmap)

                val locations = listOf(
                    GeoPoint(47.637959, 6.862894),
                    GeoPoint(47.636601, 6.856739),
                    GeoPoint(47.644654, 6.853288)
                )

                locations.forEach { location ->
                    val marker = org.osmdroid.views.overlay.Marker(this)
                    marker.position = location
                    marker.icon = scaledDrawable
                    marker.title = "Lieu"
                    marker.snippet = "Un autre endroit"
                    overlays.add(marker)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Template() {
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(

        topBar = {
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
                0 -> Recherche()
                else -> Text(
                    text = "Page ${when (selectedIndex) {
                        2 -> "Home"
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
