package com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.assignment1ChanulPiyadigama34961496.data.NutriTrackRepository
import com.fit2081.assignment1ChanulPiyadigama34961496.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeminiGenAiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutriTrackRepository(application)
    private val sessionManager = SessionManager(application)

    private val _geminiState = MutableStateFlow<GeminiState>(GeminiState.Initial)
    val geminiState: StateFlow<GeminiState> = _geminiState

    fun generateTip() {
        viewModelScope.launch {
            _geminiState.value = GeminiState.Loading
            try {
                val result = repository.generateAndSaveTip()

                result.fold(
                    onSuccess = { tip ->
                        _geminiState.value = GeminiState.Success(tip)
                    },
                    onFailure = { error ->
                        Log.d("GeminiGenAiViewModel", "Error generating tip: ${error.message}")
                        _geminiState.value = GeminiState.Error(error.message ?: "Failed to generate tip")
                    }
                )
            } catch (e: Exception) {
                _geminiState.value = GeminiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadTipHistory() {
        viewModelScope.launch {
            _geminiState.value = GeminiState.Loading
            try {
                val userId = sessionManager.getUserId() ?: throw Exception("User not logged in")
                val tips = repository.getAllPatientsTips(userId)
                _geminiState.value = GeminiState.TipsHistory(tips)
            } catch (e: Exception) {
                _geminiState.value = GeminiState.Error(e.message ?: "Failed to load tips history")
            }
        }
    }

    //generates patterns based on patient data
    fun generatePatternsBasedOnData() {
        viewModelScope.launch {
            _geminiState.value = GeminiState.Loading
            try {
                val patterns = repository.analyzePatientDataPatterns()
                _geminiState.value = GeminiState.PatternAnalysis(patterns)
            } catch (e: Exception) {
                _geminiState.value = GeminiState.Error(
                    e.message ?: "Failed to analyze patterns"
                )
            }
        }
    }

    fun clearTipHistory() {
        viewModelScope.launch {
            _geminiState.value = GeminiState.Loading
            try {
                val userId = sessionManager.getUserId() ?: throw Exception("User not logged in")
                repository.clearTipHistory(userId)
                loadTipHistory() // Refresh the tips list
            } catch (e: Exception) {
                _geminiState.value = GeminiState.Error("Failed to clear tips: ${e.message}")
            }
        }
    }

    sealed class GeminiState {
        object Initial : GeminiState()
        object Loading : GeminiState()
        data class Success(val tip: String) : GeminiState()
        data class TipsHistory(val tips: List<String>) : GeminiState()
        data class PatternAnalysis(val patterns: List<String>) : GeminiState()
        data class Error(val message: String) : GeminiState()
    }
}