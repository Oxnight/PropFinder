import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.propfinder.R
import com.example.propfinder.presentation.viewmodels.AuthViewModel
import com.example.propfinder.presentation.viewmodels.ProfileViewModel

@Composable
fun ProfilePage(
    viewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    navController: NavController
) {
    val utilisateur = viewModel.utilisateur

    Log.d("ProfilePage", "Utilisateur observé : $utilisateur")

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
            if (utilisateur != null) {
                // En-tête avec avatar + nom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.avatar1),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Black, CircleShape)
                        )
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter une photo",
                            tint = Color.Black,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(16.dp)
                                .background(Color.White, CircleShape)
                                .padding(2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "${utilisateur.prenom} ${utilisateur.nom}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bloc d’infos profil
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text("Votre profil", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileField("Prénom", utilisateur.prenom)
                    ProfileField("Nom", utilisateur.nom)
                    ProfileField("Age", utilisateur.age)
                    ProfileField("Mail", utilisateur.mail)
                }
            } else {
                CircularProgressIndicator(color = Color.White)
            }
        }


        if (utilisateur != null) {
            Button(
                onClick = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("profile_route") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .height(40.dp)
            ) {
                Text("Déconnexion", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}




@Composable
fun ProfileField(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_edit), // remplace avec un icône crayon
            contentDescription = "Edit",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$label : ",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(text = value, color = Color.Black)
    }
}