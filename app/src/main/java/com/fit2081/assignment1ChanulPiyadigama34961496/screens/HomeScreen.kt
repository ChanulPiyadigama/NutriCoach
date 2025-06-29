package com.fit2081.assignment1ChanulPiyadigama34961496.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.assignment1ChanulPiyadigama34961496.R
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.AuthViewModel

@Composable
fun HomeScreen(navController: NavController) {

    val authViewModel: AuthViewModel = viewModel()
    val currentUser = authViewModel.currentLoggedInUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = when (val state = currentUser.value) {
                is AuthViewModel.CurrentUserState.Success -> "Hello, ${state.user.name}"
                else -> "Hello"
            },
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        Row(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = "You've already filled in your food intake questionnaire, but you can change details here:",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Button(
                onClick = { navController.navigate("foodQuestionnaire") }
            ) {
                Text("Update")
            }
        }


        Image(
            painter = painterResource(id = R.drawable.foodplate),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier
                .fillMaxWidth()
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "My Score",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (val state = currentUser.value) {
                        is AuthViewModel.CurrentUserState.Success -> {
                            if (state.user.sex == "Female") {
                                "${state.user.heiTotalScoreFemale}"
                            } else {
                                "${state.user.heiTotalScoreMale}"
                            }
                        }
                        else -> "N/A"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { navController.navigate("insights") },
                modifier = Modifier.padding(start = 16.dp),
                colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Insights")
            }
        }



        Text(
            "Your Food Quality Score",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Text("Your food quality score provides a snapshot of how well your eating patterns align " +
                "with dietary guidelines. Explore the Insights section for detailed breakdown and personalized recommendations.")
    }
}


