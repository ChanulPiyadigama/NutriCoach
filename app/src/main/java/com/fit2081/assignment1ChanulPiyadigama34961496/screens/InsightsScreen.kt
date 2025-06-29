package com.fit2081.assignment1ChanulPiyadigama34961496.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.AuthViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.PatientViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.Score


@Composable
fun InsightsScreen(navController: NavController) {
    Log.d("InsightsScreen", "heifaScores")

    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val currentUser = authViewModel.currentLoggedInUser.collectAsState()

    //get access to the patientState from the viewmodel, which is all the information
    //about the current logged in user, if we change the user's info this page will be updated automatically.
    val patientViewModel: PatientViewModel = viewModel()
    val heifaScores = patientViewModel.heifaScores.collectAsState(initial = emptyList())
    val patientDataState = patientViewModel.patientDataState.collectAsState()


    when (val state = patientDataState.value) {
        is PatientViewModel.PatientDataState.Loading -> {
            // Show loading indicator
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Loading your health insights...", modifier = Modifier.padding(top = 16.dp))
            }
        }

        is PatientViewModel.PatientDataState.Loaded -> {

            //lazy column for scrolling, automatically stacks in column and renders only visible items,
            //like lazygrid you have to pass composables to it through item{}
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Your Nutrition Scores",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                items(heifaScores.value) { score ->
                    if (score.name == "Total Score") {
                        TotalScoreProgressBar(score)
                    } else {
                        ScoreProgressBar(score)
                    }
                }

                // Add buttons at the bottom of scrollable content
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                val shareText = when (currentUser.value) {
                                    is AuthViewModel.CurrentUserState.Success -> {
                                        "My Nutrition Total Score: ${heifaScores.value.find { it.name == "Total Score" }?.userScore ?: 0}"
                                    }
                                    else -> "My Nutrition Total Score: Not available"
                                }
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Share text via"
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share Insights")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { navController.navigate("nutriCoach") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Improve my diet")
                        }
                    }
                }

                // Add bottom padding for better scrolling experience
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        is PatientViewModel.PatientDataState.Error -> {
            // Show error state
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Error loading insights: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { navController.navigate("home") }) {
                    Text("Return to Home")
                }
            }
        }
        else -> {}
    }
}


@Composable
fun ScoreProgressBar(score: Score) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = score.name)
            Text(text = "${score.userScore}/${score.maxScore}")
        }

        LinearProgressIndicator(
            progress = { (score.userScore / score.maxScore).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}


@Composable
fun TotalScoreProgressBar(score: Score) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = score.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "${score.userScore}/${score.maxScore}",
                style = MaterialTheme.typography.titleLarge
            )
        }

        LinearProgressIndicator(
            //math calculate the fraction and make sure its between 0 and 1
            progress = { (score.userScore / score.maxScore).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}