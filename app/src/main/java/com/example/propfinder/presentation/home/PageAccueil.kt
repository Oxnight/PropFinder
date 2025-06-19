package com.example.propfinder.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import com.example.propfinder.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.example.propfinder.presentation.viewmodels.AnnonceViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import com.example.propfinder.data.models.Annonce

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Recherche(annonceViewModel: AnnonceViewModel, onAnnonceClick: (String) -> Unit ) {
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tous") }
    var showDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E))
        ){
            Column(){
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {},
                        title = { Text("Filtrer les annonces") },
                        text = {
                            Column {
                                listOf("Tous", "à louer", "à vendre").forEach { type ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedFilter = type
                                                showDialog = false
                                            }
                                            .padding(8.dp)
                                    ) {
                                        RadioButton(
                                            selected = selectedFilter == type,
                                            onClick = {
                                                selectedFilter = type
                                                showDialog = false
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(type)
                                    }
                                }
                            }
                        }
                    )
                }
                Row(modifier = Modifier.padding(16.dp),){
                    TextField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = { Text("Search...") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f) //
                            .height(56.dp),
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // petit espace entre le champ et l'image

                    // ajouter le bouton de filtre
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtrer",
                            modifier = Modifier.size(56.dp),
                            tint = Color(0xFFF07B42)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                }
                Row(modifier = Modifier.padding(16.dp)){
                    Annonce(viewModel = annonceViewModel, onAnnonceClick = onAnnonceClick, selectedFilter = selectedFilter, search = search)
                }
            }
        }
    }
}

@Composable
fun Annonce(viewModel: AnnonceViewModel, onAnnonceClick: (String) -> Unit, selectedFilter: String, search: String) {
    var annonces by remember { mutableStateOf<List<Annonce>>(emptyList()) }

    LaunchedEffect(selectedFilter, search) {
        viewModel.getAllAnnonces { allAnnonces ->
            val filtered = when (selectedFilter) {
                "à louer" -> allAnnonces.filter { it.type == "à louer" }
                "à vendre" -> allAnnonces.filter { it.type == "à vendre" }
                else -> allAnnonces
            }

            annonces = if (search.isNotBlank()) {
                filtered.filter { it.titre?.contains(search, ignoreCase = true) == true }
            } else {
                filtered
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(annonces) { annonce ->
            val titre = annonce.titre?.toString() ?: ""
            val description = annonce.description?.toString() ?: ""
            val localisation = annonce.localisation?.toString() ?: ""
            val prix = annonce.prix?.toString() ?: ""
            val dateLisible = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(annonce.date))

            AnnonceItem(
                title = titre,
                localisation = localisation,
                prix = prix + " €",
                date = dateLisible,
                type = annonce.type,
                onClick = { onAnnonceClick(titre) }
            )
        }
    }
}

@Composable
fun AnnonceItem(
    title: String,
    localisation: String,
    prix: String,
    date: String,
    type: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Partie haute : image + infos
            Row(modifier = Modifier.fillMaxWidth()) {

                // Image
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.appartement1),
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Badge dispo
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                color = when (type) {
                                    "à louer" -> Color(0xFF2196F3)  // Bleu
                                    "à vendre" -> Color(0xFFF44336) // Rouge
                                    else -> Color.Gray
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (type) {
                                "à louer" -> "À louer"
                                "à vendre" -> "À vendre"
                                else -> "Annonce"
                            },
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Texte
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = localisation,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(10.dp))

            // Bas : prix + date avec icônes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFFF07B42))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = prix, color = Color(0xFFF07B42), style = MaterialTheme.typography.titleMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = date, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}