package com.example.propfinder.presentation.main.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.example.propfinder.R
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel

@Composable
fun OsmdroidMapView(annonceViewModel: AnnonceViewModel) {
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