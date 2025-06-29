package com.fit2081.assignment1ChanulPiyadigama34961496.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.GeminiGenAiViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.PatientViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun AdminScreen(navController: NavController) {
    val patientViewModel: PatientViewModel = viewModel()
    val averageScores by patientViewModel.averageHeifaScores.collectAsState()
    val patientDataState by patientViewModel.patientDataState.collectAsState()

    val geminiViewModel: GeminiGenAiViewModel = viewModel()
    val geminiState by geminiViewModel.geminiState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Clinician Dashboard",
            style = MaterialTheme.typography.headlineLarge
        )

        when (patientDataState) {
            is PatientViewModel.PatientDataState.Loading -> {
                Text(
                    "Loading averages...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is PatientViewModel.PatientDataState.Error -> {
                Text(
                    text = (patientDataState as PatientViewModel.PatientDataState.Error).message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            PatientViewModel.PatientDataState.Initial,
            PatientViewModel.PatientDataState.Loaded -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Male Average",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "${averageScores.maleMeanScore ?: "No data"}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = MaterialTheme.typography.headlineMedium.fontWeight
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Female Average",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "${averageScores.femaleMeanScore ?: "No data"}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = MaterialTheme.typography.headlineMedium.fontWeight
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { geminiViewModel.generatePatternsBasedOnData() }
        ) {
            Text("Find Data Patterns")
        }

        when (geminiState) {
            is GeminiGenAiViewModel.GeminiState.Loading -> {
                Text(
                    "Analyzing patterns...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is GeminiGenAiViewModel.GeminiState.PatternAnalysis -> {
                val patterns = (geminiState as GeminiGenAiViewModel.GeminiState.PatternAnalysis).patterns
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    patterns.forEach { pattern ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = pattern,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
            is GeminiGenAiViewModel.GeminiState.Error -> {
                Text(
                    text = (geminiState as GeminiGenAiViewModel.GeminiState.Error).message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Done")
        }
    }
}
