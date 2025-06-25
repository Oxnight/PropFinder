package com.example.propfinder.presentation.main.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.example.propfinder.R
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel

@Composable
fun OsmdroidMapView(
    annonceViewModel: AnnonceViewModel,
    navController: NavController,
    onAnnonceClick: (String) -> Unit
) {

    // Intégration d'une vue Android native (MapView) dans Compose
    AndroidView(
        factory = { context: Context ->
            MapView(context).apply {
                // Configuration de la source des tuiles de la carte
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true) // Activation du multitouch (zoom par pincement)
                controller.setZoom(16.0) // Zoom initial
                controller.setCenter(GeoPoint(47.637959, 6.862894)) // Position centrale par défaut (ex : Belfort)

                // Chargement et redimensionnement de l'icône personnalisée de localisation
                val drawable = resources.getDrawable(R.drawable.pinpoint) as BitmapDrawable
                val bitmap = drawable.bitmap
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 40, 50, false)
                val scaledDrawable = BitmapDrawable(resources, scaledBitmap)

                // Récupération de toutes les coordonnées des annonces depuis le ViewModel
                annonceViewModel.getAllCoordinates { coordinatesList ->
                    coordinatesList.forEach { coord ->
                        val parts = coord.split(",")
                        if (parts.size == 2) {
                            // Conversion des coordonnées en latitude/longitude
                            val latitude = parts[0].toDoubleOrNull()
                            val longitude = parts[1].toDoubleOrNull()

                            if (latitude != null && longitude != null) {
                                val geoPoint = GeoPoint(latitude, longitude)
                                var clickCounts = mutableMapOf<String, Int>() // Gestion des clics par titre

                                // Récupération des données de l’annonce (localisation et titre) via les coordonnées
                                annonceViewModel.getLocalisationByCoordonnees(coord) { localisation ->
                                    annonceViewModel.getTitleByCoordonnees(coord) { titre ->
                                        // Création du marqueur sur la carte
                                        val marker = org.osmdroid.views.overlay.Marker(this)
                                        marker.position = geoPoint
                                        marker.icon = scaledDrawable // Utilisation de l'icône personnalisée
                                        marker.title = titre ?: "Titre inconnu"
                                        marker.snippet = localisation ?: "Adresse inconnue"

                                        // Définition du comportement au clic sur le marqueur
                                        marker.setOnMarkerClickListener { clickedMarker, _ ->
                                            val title = clickedMarker.title ?: return@setOnMarkerClickListener false

                                            // Réinitialisation des clics pour les autres marqueurs
                                            clickCounts.keys.forEach { key ->
                                                if (key != title) clickCounts[key] = 0
                                            }

                                            val count = (clickCounts[title] ?: 0) + 1
                                            clickCounts[title] = count

                                            if (count == 1) {
                                                // Premier clic : affiche la bulle d'info
                                                clickedMarker.showInfoWindow()
                                                true
                                            } else if (count == 2) {
                                                // Deuxième clic : déclenche la navigation ou action via callback
                                                onAnnonceClick(title)
                                                clickCounts[title] = 0
                                                true
                                            } else {
                                                false
                                            }
                                        }

                                        // Ajout du marqueur à la carte
                                        overlays.add(marker)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize() // La carte occupe tout l’espace disponible
    )
}