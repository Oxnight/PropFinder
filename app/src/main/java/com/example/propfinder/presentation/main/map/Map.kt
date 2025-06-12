package com.example.propfinder.presentation.main.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
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
fun OsmdroidMapView(annonceViewModel: AnnonceViewModel, navController : NavController, onAnnonceClick: (String) -> Unit){

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

                annonceViewModel.getAllCoordinates { coordinatesList ->
                    coordinatesList.forEach { coord ->
                        val parts = coord.split(",") // Supposons que les coordonnées soient au format "latitude,longitude"
                        if (parts.size == 2) {
                            val latitude = parts[0].toDoubleOrNull()
                            val longitude = parts[1].toDoubleOrNull()
                            if (latitude != null && longitude != null) {
                                val geoPoint = GeoPoint(latitude, longitude)
                                var clickCounts = mutableMapOf<String, Int>()

                                annonceViewModel.getLocalisationByCoordonnees(coord) { localisation ->
                                    annonceViewModel.getTitleByCoordonnees(coord) { titre ->
                                        val marker = org.osmdroid.views.overlay.Marker(this)
                                        marker.position = geoPoint
                                        marker.icon = scaledDrawable
                                        marker.title = titre ?: "Titre inconnu"
                                        marker.snippet = localisation ?: "Adresse inconnue"

                                        marker.setOnMarkerClickListener { clickedMarker, _ ->
                                            val title = clickedMarker.title ?: return@setOnMarkerClickListener false

                                            clickCounts.keys.forEach { key ->
                                                if (key != title) clickCounts[key] = 0
                                            }

                                            val count = (clickCounts[title] ?: 0) + 1
                                            clickCounts[title] = count

                                            if (count == 1) {
                                                clickedMarker.showInfoWindow()
                                                true
                                            } else if (count == 2) {
                                                onAnnonceClick(title)
                                                clickCounts[title] = 0
                                                true
                                            } else {
                                                false
                                            }
                                        }


                                        overlays.add(marker)
                                    }
                                }

                            }
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}