package com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.assignment1ChanulPiyadigama34961496.data.NutriTrackRepository
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.FruitModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

class FruityViceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutriTrackRepository(application)

    private val _fruitState = MutableStateFlow<FruitState>(FruitState.Initial)
    val fruitState: StateFlow<FruitState> = _fruitState

    fun searchFruit(name: String) {
        val cleanedName = name.trim()
        viewModelScope.launch {
            _fruitState.value = FruitState.Loading
            try {
                val result = repository.getFruitInfo(cleanedName)
                //we receive data wrapped in a Result object, so we can use fold to handle both success and failure
                result.fold(
                    onSuccess = { fruit ->
                        _fruitState.value = FruitState.Success(fruit)
                    },
                    //if error, based on the code set UserMessage to the right one.
                    onFailure = { error ->
                        val userMessage = when (error) {
                            is HttpException -> when (error.code()) {
                                404 -> "Fruit not found. Please check the spelling and try again."
                                500 -> "Server error. Please try again later."
                                503 -> "Service temporarily unavailable. Please try again later."
                                else -> "Unable to fetch fruit information. Please try again."
                            }
                            is UnknownHostException -> "No internet connection. Please check your network."
                            else -> "An unexpected error occurred. Please try again."
                        }
                        _fruitState.value = FruitState.Error(userMessage)
                    }
                )
            } catch (e: Exception) {
                val userMessage = when (e) {
                    is UnknownHostException -> "No internet connection. Please check your network."
                    else -> "An unexpected error occurred. Please try again."
                }
                _fruitState.value = FruitState.Error(userMessage)
            }
        }
    }

    sealed class FruitState {
        object Initial : FruitState()
        object Loading : FruitState()
        data class Success(val fruit: FruitModel) : FruitState()
        data class Error(val message: String) : FruitState()
    }
}