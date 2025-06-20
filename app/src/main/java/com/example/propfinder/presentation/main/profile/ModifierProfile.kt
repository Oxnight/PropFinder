package com.example.propfinder.presentation.main.profile

import androidx.compose.material3.TextFieldDefaults

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.propfinder.presentation.viewmodels.ProfileViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext


@Composable
fun ModifierProfilePage(
    navController: NavController,
    profilviewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current

    var prenom = remember { mutableStateOf("") }
    var nom = remember { mutableStateOf("") }
    var age = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp)) // padding haut

            Text(
                text = "Modifier votre profil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                val utilisateur = profilviewModel.getUserProfile()
                EditableField("Prénom",prenom.apply { value = utilisateur?.prenom ?: "" })
                EditableField("Nom",nom.apply { value = utilisateur?.nom ?: "" })
                EditableField("Âge",age.apply { value = utilisateur?.age ?: "" })
                EditableField("Email",email.apply { value = utilisateur?.mail ?: "" })
            }

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    if (
                        prenom.value.isNotBlank() ||
                        nom.value.isNotBlank() ||
                        age.value.isNotBlank() ||
                        email.value.isNotBlank()
                    ) {
                        profilviewModel.updateUserProfile(
                            prenom = prenom.value,
                            nom = nom.value,
                            age = age.value,
                            email = email.value
                        )
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Au moins un champ doit être rempli", Toast.LENGTH_SHORT).show()
                    }
                }
                ,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Enregistrer", color = Color.White, fontSize = 16.sp)
            }


            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Annuler", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun EditableField(label: String, text : MutableState<String>) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {

        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(6.dp),
        )
    }
}

