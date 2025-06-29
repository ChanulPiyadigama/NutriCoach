package com.fit2081.assignment1ChanulPiyadigama34961496.screens.FoodQuestionnaireScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.FoodIntakeViewModel


@Composable
fun FoodQuestionnaireScreen(navController: NavController) {
    val foodIntakeViewModel: FoodIntakeViewModel = viewModel()
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val foodIntakeState by foodIntakeViewModel.foodIntakeState.collectAsState()


    val personas = mapOf(
        "Health Devotee" to "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
        "Mindful Eater" to "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
        "Wellness Striver" to "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.",
        "Balance Seeker" to "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
        "Health Procrastinator" to "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I’m not motivated to make it a high priority because I have too many other things going on in my life.",
        "Food Carefree" to "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat."
    )


    var selectedPersona by remember { mutableStateOf("") }

    //state for each question is a map
    val timingQuestions = remember {
        mutableStateMapOf(
            "What time of day approx. do you normally eat your biggest meal?" to "00:00",
            "What time of day approx. do you go to sleep at night" to "00:00",
            "What time of day approx. do you wake up in the morning" to "00:00"
        )
    }

    //state for each category linked to a checkbox through map
    val checkedCategories = remember {
        mutableStateMapOf(
            "Fruits" to false,
            "Vegetables" to false,
            "Grains" to false,
            "Protein" to false,
            "Dairy" to false,
            "Sweets" to false,
            "Seafood" to false,
            "Snacks" to false,
            "Beverages" to false
        )
    }

    // Function to auto-save data so users progress is not lost, its passed
    //to each composable and they use it in their onChange methods
    fun autoSaveData() {

        foodIntakeViewModel.saveFoodIntake(
            foodCategories = checkedCategories.toMap(),
            persona = selectedPersona,
            timings = timingQuestions.toMap()
        )
    }



    LaunchedEffect(foodIntakeState) {
        when (foodIntakeState) {
            //when questionnaire screen opens, grab intake answers from db and load it if it exists,
            //should only run once when screen opens, viewmodel state is not changed upon autosave, as its unnecessary (for now)
            is FoodIntakeViewModel.FoodIntakeState.Loaded -> {
                //LoadedState comes with the foodIntake object passed to it from the viewmodel
                val foodIntake = (foodIntakeState as FoodIntakeViewModel.FoodIntakeState.Loaded).foodIntake
                selectedPersona = foodIntake.persona

                // Load categories from the model
                foodIntake.foodCategories.forEach { (category, isChecked) ->
                    if (checkedCategories.containsKey(category)) {
                        checkedCategories[category] = isChecked
                    }
                }

                // Load timings from the model
                foodIntake.timings.forEach { (question, time) ->
                    if (timingQuestions.containsKey(question)) {
                        timingQuestions[question] = time
                    }
                }
            }

            //runs if its the users first ever time (their entry doesnt exist in the db)
            else -> {} // Handle other states if needed
        }
    }

    fun validateForm(): Boolean {
        if (selectedPersona.isEmpty()) {
            errorMessage = "Please select your persona"
            return false
        }
        if (checkedCategories.values.none { it }) {
            errorMessage = "Please select at least one food category"
            return false
        }
        if (timingQuestions.values.any { it == "00:00" }) {
            errorMessage = "Please answer all timing questions"
            return false
        }
        // All times should be unique
        val uniqueTimes = timingQuestions.values.toSet()
        if (uniqueTimes.size < timingQuestions.size) {
            errorMessage = "Please select different times for each question"
            return false
        }
        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Food Intake Questionnaire", style = MaterialTheme.typography.headlineLarge)


        //all question sections get passed in the autoSaveData so any changes are saved to the db
        //so user progress is not lost

        CategorySelectionComponent(
            checkedCategories = checkedCategories,
            onCategoryChanged = { autoSaveData() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PersonaSelection(
            personas = personas,
            selectedPersona = selectedPersona,
            onPersonaSelected = { newValue ->
                selectedPersona = newValue
                autoSaveData()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimingQuestions(
            timingQuestionsMap = timingQuestions,
            onTimeChanged = { autoSaveData() }
        )

        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(onClick = {
            if (validateForm()) {
                navController.navigate("home")
            } else {
                showError = true
            }
        }) {
            Text("Save")
        }

    }
}







