package com.example.propfinder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LoginPage(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.propfinder),
                contentDescription = "Logo",
                modifier = Modifier.size(128.dp) // ou autre taille
            )
            Text(
                text = "Connexion",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("votre.email@example.com") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                placeholder = { Text("••••••••") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* À connecter plus tard */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42))        ) {
                Text("Se connecter", color = Color(0xFF1E1E1E))
            }
            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42))        ) {
                Text("Créer un compte", color = Color(0xFF1E1E1E))
            }
            Text(
                text = "Mot de passe oublié ?",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun RegisterPage() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var first_name by remember { mutableStateOf("") }
    var last_name by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)) // ✅ toute la page est noire
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.propfinder),
                contentDescription = "Logo",
                modifier = Modifier.size(128.dp) // ou autre taille
            )
            Text(
                text = "Créer un compte",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("votre.email@example.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = last_name,
                onValueChange = { last_name = it },
                label = { Text("Nom") },
                placeholder = { Text("Dupont") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = first_name,
                onValueChange = { first_name = it },
                label = { Text("Prénom") },
                placeholder = { Text("Jean") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                placeholder = { Text("50") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                placeholder = { Text("••••••••") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = { /* a changer plus tard */ },
                modifier = Modifier.fillMaxWidth()
                    .padding(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B42))        ) {
                Text("Créer votre compte", color = Color(0xFF1E1E1E))
            }
        }
    }
}
