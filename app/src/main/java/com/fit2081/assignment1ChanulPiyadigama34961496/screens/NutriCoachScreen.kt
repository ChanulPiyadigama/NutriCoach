package com.fit2081.assignment1ChanulPiyadigama34961496.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.AuthViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.FruityViceViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.GeminiGenAiViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriCoachScreen(navController: NavController) {
    //for sending reqs to fruityvice api to get information, updating ui based on state
    val fruityViceViewModel: FruityViceViewModel = viewModel()
    val fruitState by fruityViceViewModel.fruitState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }


    //for getting tip from gemini, updating ui based on state
    val geminiGenViewModel: GeminiGenAiViewModel = viewModel()
    val geminiState by geminiGenViewModel.geminiState.collectAsState()
    var showTipsModal by remember { mutableStateOf(false) }

    //for checking scores of user
    val authViewModel: AuthViewModel = viewModel()
    val currentUser = authViewModel.currentLoggedInUser.collectAsState()

    val randomSeed = remember { System.currentTimeMillis() }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        when (val state = currentUser.value) {
            is AuthViewModel.CurrentUserState.Success -> {
                val hasOptimalScore = if (state.user.sex == "Female") {
                    state.user.fruitHEIFAScoreFemale == 10.0
                } else {
                    state.user.fruitHEIFAScoreMale == 10.0
                }

                if (hasOptimalScore) {
                    // Use aysnc image to display random image due to simplicity, grabs data from
                    //api off the main thread and displays it. Uses picsums random parameter to get random img.
                    AsyncImage(
                        model = "https://picsum.photos/300/200?random=$randomSeed",
                        contentDescription = "Random congratulatory image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Show fruit search section for non-optimal score
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search fruit") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { fruityViceViewModel.searchFruit(searchQuery) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Search")
                    }

                    when (val fruitState = fruitState) {
                        is FruityViceViewModel.FruitState.Loading -> {
                            CircularProgressIndicator()
                        }
                        is FruityViceViewModel.FruitState.Success -> {
                            val fruit = fruitState.fruit
                            Card(modifier = Modifier.padding(8.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Fruit: ${fruit.name}")
                                    Text("Calories: ${fruit.nutritions.calories}")
                                    Text("Sugar: ${fruit.nutritions.sugar}g")
                                    Text("Protein: ${fruit.nutritions.protein}g")
                                    Text("Carbohydrates: ${fruit.nutritions.carbohydrates}g")
                                    Text("Fat: ${fruit.nutritions.fat}g")
                                }
                            }
                        }
                        is FruityViceViewModel.FruitState.Error -> {
                            Log.d("ERROR", fruitState.message)
                            Text(fruitState.message, color = MaterialTheme.colorScheme.error)
                        }
                        else -> {}
                    }
                }
            }
            else -> {
                Text("Unable to load  data", color = MaterialTheme.colorScheme.error)
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        // Tips Section
        Text(
            "AI Nutrition Tips",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { geminiGenViewModel.generateTip() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Motivational Message")
        }

        // Display generated tip
        when (geminiState) {
            is GeminiGenAiViewModel.GeminiState.Success -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        (geminiState as GeminiGenAiViewModel.GeminiState.Success).tip,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is GeminiGenAiViewModel.GeminiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is GeminiGenAiViewModel.GeminiState.Error -> {
                Text(
                    (geminiState as GeminiGenAiViewModel.GeminiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {}
        }

        Button(
            onClick = {
                showTipsModal = true
                geminiGenViewModel.loadTipHistory()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Show All Tips")
        }
    }

    // Tips History Modal
    if (showTipsModal) {
        ModalBottomSheet(
            onDismissRequest = { showTipsModal = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Your Tips History",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (val state = geminiState) {
                    //HD FEATURE, CLEAR TIP HISTORY
                    is GeminiGenAiViewModel.GeminiState.TipsHistory -> {
                        if (state.tips.isEmpty()) {
                            Text("No tips history yet")
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {
                                items(state.tips) { tip ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = tip,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { geminiGenViewModel.clearTipHistory() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Clear All Tips")
                        }
                    }
                    is GeminiGenAiViewModel.GeminiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is GeminiGenAiViewModel.GeminiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}