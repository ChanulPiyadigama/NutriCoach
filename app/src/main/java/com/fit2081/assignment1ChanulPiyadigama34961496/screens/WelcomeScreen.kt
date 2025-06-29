package com.fit2081.assignment1ChanulPiyadigama34961496.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.assignment1ChanulPiyadigama34961496.R
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.AuthViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.FoodIntakeViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun WelcomeScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val currentUserState by authViewModel.currentLoggedInUser.collectAsState()

    val foodIntakeViewModel: FoodIntakeViewModel = viewModel()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "NutriTrack",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.nutritrack_logo),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(170.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This app provides general health and nutrition information for\n" +
                    "educational purposes only. It is not intended as medical advice,\n" +
                    "diagnosis, or treatment. Always consult a qualified healthcare\n" +
                    "professional before making any changes to your diet, exercise, or\n" +
                    "health regimen.\n" +
                    "Use this app at your own risk.\n" +
                    "If you'd like to an Accredited Practicing Dietitian (APD), please\n" +
                    "visit the Monash Nutrition/Dietetics Clinic (discounted rates for\n" +
                    "students): \n",
            style = TextStyle(
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition",
            style = TextStyle(
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic
            ),
            textAlign = TextAlign.Center
        )

        if (currentUserState is AuthViewModel.CurrentUserState.Error) {
            Text(
                text = (currentUserState as AuthViewModel.CurrentUserState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = TextStyle(fontSize = 14.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                when (currentUserState) {
                    is AuthViewModel.CurrentUserState.Success -> {
                        foodIntakeViewModel.validateFoodIntake(
                            (currentUserState as AuthViewModel.CurrentUserState.Success).user.userId
                        ) { isComplete ->
                            if (isComplete) {
                                navController.navigate("home") {
                                    popUpTo(0)
                                }
                            } else {
                                navController.navigate("foodQuestionnaire") {
                                    popUpTo(0)
                                }
                            }
                        }
                    }
                    is AuthViewModel.CurrentUserState.NotLoggedIn -> {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                    else -> {} // Handle other states by staying on welcome screen
                }
            },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("Login")
        }

        Text("Designed by Chanul Piyadigama 34961496")
    }
}
