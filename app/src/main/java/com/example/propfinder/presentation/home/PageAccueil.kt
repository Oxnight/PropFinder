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
            val date = annonce.date?.toString() ?: ""

            AnnonceItem(
                title = titre,
                description = description,
                localisation = localisation,
                prix = prix,
                date = date,
                onClick = { onAnnonceClick(titre) }
            )
        }
    }
}

@Composable
fun AnnonceItem(
    title: String,
    description: String,
    localisation: String,
    prix: String,
    date: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFD9D9D9))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.appartement1),
                contentDescription = "Image par défaut",
                modifier = Modifier
                    .height(120.dp)
                    .padding(end = 8.dp)
            )
            Column {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(localisation, color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                Text(prix, color = Color(0xFFF07B42), style = MaterialTheme.typography.titleMedium)
                Text(date, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/*
@Composable
fun Annonce(viewModel: AnnonceViewModel, onAnnonceClick: (String) -> Unit) {
    var description by remember { mutableStateOf("") }
    var localisation by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var prix by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getAnnonceById("9OIiQkO26d5Ok9LfpHC2") { data: Map<String, Any?>? ->
            data?.let {
                description = it["description"]?.toString() ?: "N/A"
                localisation = it["localisation"]?.toString() ?: "N/A"
                date = it["date"]?.toString() ?: "N/A"
                prix = it["prix"]?.toString() ?: "N/A"
                title = it["titre"]?.toString() ?: "N/A"
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFD9D9D9))
            .clickable{
                onAnnonceClick(title)
            }
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.appartement1),
                contentDescription = "Image par défaut",
                modifier = Modifier
                    .height(120.dp)
                    .padding(end = 8.dp)
            )
            Column {
                //Text("$description", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "$description",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge
                )
                Text("$localisation", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                Text("$prix", color = Color(0xFFF07B42), style = MaterialTheme.typography.titleMedium)
                Text("$date", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}*/


/*
@Composable
fun AnnonceCard(annonce: com.example.propfinder.data.models.Annonce) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(annonce.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD9D9D9)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column {
            // Image de l'annonce
            if (annonce.images.isNotEmpty()) {
                AsyncImage(
                    model = annonce.images.first(),
                    contentDescription = "Image de l'annonce",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.appartement1),
                    placeholder = painterResource(id = R.drawable.appartement1)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.Gray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.appartement1),
                        contentDescription = "Image par défaut",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Informations de l'annonce
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = annonce.titre,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = annonce.localisation,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${annonce.prix.toInt()}€",
                        color = Color(0xFFF07B42),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = annonce.description,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = annonce.type,
                        color = Color(0xFF1E1E1E),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Publié le $formattedDate",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

 */