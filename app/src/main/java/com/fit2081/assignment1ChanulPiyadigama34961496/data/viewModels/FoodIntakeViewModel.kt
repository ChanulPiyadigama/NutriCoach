package com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.assignment1ChanulPiyadigama34961496.data.NutriTrackRepository
import com.fit2081.assignment1ChanulPiyadigama34961496.data.SessionManager
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.FoodIntake
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodIntakeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutriTrackRepository(application)
    private val sessionManager = SessionManager(application)

    private val _foodIntakeState = MutableStateFlow<FoodIntakeState>(FoodIntakeState.Initial)
    val foodIntakeState: StateFlow<FoodIntakeState> = _foodIntakeState

    init {
        loadFoodIntake()
    }

    private fun loadFoodIntake() {
        //coroutines are launched in the viewModelScope, which is tied to the lifecycle of the ViewModel,
        //so when the ViewModel is cleared, such as navigating away from a screen, the coroutine is also cancelled
        viewModelScope.launch {
            //get the userId, and send their current food intake answers to the UI, if its the users
            //first time their entry wont exist, thus dont return anything
            try {
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    val foodIntake = repository.getFoodIntakeByPatientId(userId)
                    if (foodIntake != null) {
                        _foodIntakeState.value = FoodIntakeState.Loaded(foodIntake)
                    } else {
                        _foodIntakeState.value = FoodIntakeState.Empty
                    }
                } else {
                    _foodIntakeState.value = FoodIntakeState.Error("User not logged in")
                }
            } catch (e: Exception) {
                _foodIntakeState.value = FoodIntakeState.Error("Failed to load food intake: ${e.message}")
            }
        }
    }

    fun saveFoodIntake(foodCategories: Map<String, Boolean>, persona: String, timings: Map<String, String>) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId() ?: return@launch
                val existingIntake = repository.getFoodIntakeByPatientId(userId)

                if (existingIntake != null) {
                    // Create updated version preserving the original ID
                    val updatedIntake = existingIntake.copy(
                        foodCategories = foodCategories,
                        persona = persona,
                        timings = timings
                    )
                    repository.updateFoodIntake(updatedIntake)
                } else {
                    // Create new record only if one doesn't exist
                    val foodIntake = FoodIntake(
                        patientId = userId,
                        foodCategories = foodCategories,
                        persona = persona,
                        timings = timings
                    )
                    repository.insertFoodIntake(foodIntake)
                }
            } catch (e: Exception) {
                _foodIntakeState.value = FoodIntakeState.Error("Failed to save food intake: ${e.message}")
            }
        }
    }

    //validation of the food intake completion is moved from the ui to the viewModel,
    //to not repeat code throughout the MVVM
    fun validateFoodIntake(userId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.checkFoodIntakeCompletionByPatientId(userId)
            callback(result)
        }
    }


    sealed class FoodIntakeState {
        object Initial : FoodIntakeState()
        object Empty : FoodIntakeState()
        data class Loaded(val foodIntake: FoodIntake) : FoodIntakeState()
        data class Error(val message: String) : FoodIntakeState()
    }
}