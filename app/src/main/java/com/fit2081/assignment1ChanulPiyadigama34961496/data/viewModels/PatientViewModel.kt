package com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.assignment1ChanulPiyadigama34961496.data.NutriTrackRepository
import com.fit2081.assignment1ChanulPiyadigama34961496.data.SessionManager
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.HeifaAverages
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.Score
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NutriTrackRepository(application)
    private val sessionManager = SessionManager(application)

    //keeps track of current state of any patient data retrival
    private val _patientDataState = MutableStateFlow<PatientDataState>(PatientDataState.Initial)
    val patientDataState: StateFlow<PatientDataState> = _patientDataState

    //state flow to hold all patient IDs so it can be used throughout the app
    private val _patientIds = MutableStateFlow<List<String>>(emptyList())
    val patientIds: StateFlow<List<String>> = _patientIds

    //state flow to hold the HEIFA scores for the current user
    private val _heifaScores = MutableStateFlow<List<Score>>(emptyList())
    val heifaScores: StateFlow<List<Score>> = _heifaScores

    //state flow to hold the Average heifa scores for patients
    private val _averageHeifaScores = MutableStateFlow<HeifaAverages>(
        HeifaAverages(
            maleMeanScore = null,
            femaleMeanScore = null
        )
    )
    val averageHeifaScores: StateFlow<HeifaAverages> = _averageHeifaScores

    init {
        getAllPatientIds()
        getHeifaPatientScores()
        getAverageHeifaScores()
    }


    fun getAllPatientIds() {
        viewModelScope.launch {
            _patientDataState.value = PatientDataState.Loading
            try {
                val ids = repository.getAllPatientIds()
                _patientIds.value = ids
                _patientDataState.value = PatientDataState.Loaded  // Only  after success
            } catch (e: Exception) {
                _patientDataState.value = PatientDataState.Error("Failed to load patient IDs: ${e.message}")
            }
        }
    }

    fun getAverageHeifaScores() {
        viewModelScope.launch {
            _patientDataState.value = PatientDataState.Loading
            try {
                val averages = repository.getHeifaAverages()
                _averageHeifaScores.value = averages
                _patientDataState.value = PatientDataState.Loaded  // Only  after success
            } catch (e: Exception) {
                _patientDataState.value = PatientDataState.Error("Failed to load patient IDs: ${e.message}")
            }
        }
    }


    fun getHeifaPatientScores() {
        viewModelScope.launch {
            _patientDataState.value = PatientDataState.Loading
            try {
                val userId = sessionManager.getUserId() ?: throw Exception("No user logged in")
                val scores = repository.getGenderSpecificScores(userId)?.scores
                if (scores != null) {
                    _heifaScores.value = scores
                    _patientDataState.value = PatientDataState.Loaded
                } else {
                    _heifaScores.value = emptyList()
                    _patientDataState.value = PatientDataState.Error("Patient scores not found")
                }
            } catch (e: Exception) {
                _heifaScores.value = emptyList()
                _patientDataState.value = PatientDataState.Error("Failed to load HEIFA scores: ${e.message}")
            }
        }
    }

    sealed class PatientDataState {
        object Initial : PatientDataState()
        object Loaded : PatientDataState()
        object Loading : PatientDataState()
        data class Error(val message: String) : PatientDataState()
    }
}