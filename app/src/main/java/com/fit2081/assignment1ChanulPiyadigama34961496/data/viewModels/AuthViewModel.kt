package com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.assignment1ChanulPiyadigama34961496.data.NutriTrackRepository
import com.fit2081.assignment1ChanulPiyadigama34961496.data.PasswordManager
import com.fit2081.assignment1ChanulPiyadigama34961496.data.SessionManager
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NutriTrackRepository(application)
    private val sessionManager = SessionManager(application)
    private val passwordManager = PasswordManager(application)


    //we use stateflow so certain ui components can subscribe to changes.
    //Mutablestateflow is private so it can only be changed in the viewmodel, and an unmutable
    //stateflow is exposed to the ui, so it can only be read and not changed. When stateflow changes
    //the ui will be notified and can update itself.

    //Keeps track of the current loggedin user, for the ui to access
    private val _currentLoggedInUser = MutableStateFlow<CurrentUserState>(CurrentUserState.Initial)
    val currentLoggedInUser: StateFlow<CurrentUserState> = _currentLoggedInUser

    //Tracks how login is going, so the UI can show loading, success or error changes
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    //Tracks how registration is going, so the UI can show loading, success or error states
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _currentLoggedInUser.value = CurrentUserState.Loading
            try {
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    val user = repository.getCurrentUser()
                    if (user != null) {
                        _currentLoggedInUser.value = CurrentUserState.Success(user)
                    } else {
                        _currentLoggedInUser.value = CurrentUserState.Error("User not found")
                        sessionManager.logout()
                    }
                } else {
                    _currentLoggedInUser.value = CurrentUserState.NotLoggedIn
                }
            } catch (e: Exception) {
                _currentLoggedInUser.value = CurrentUserState.Error("Failed to load user: ${e.message}")
            }
        }
    }

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val userVerified = passwordManager.verifyPassword(userId, password)

                if (userVerified) {
                    val patient = repository.getPatientById(userId)
                    if (patient != null) {
                        sessionManager.createLoginSession(userId)
                        _currentLoggedInUser.value = CurrentUserState.Success(patient)
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error("User not found")
                    }
                } else {
                    _loginState.value = LoginState.Error("Invalid user ID or password")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }


    fun register(userId: String, phoneNumber: String, name: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                //check if user already registered
                if (repository.isAccountClaimed(userId)) {
                    _registerState.value = RegisterState.Error("Account already claimed")
                    return@launch
                }

                //if validated, log them in
                val preRegisteredUser = repository.validatePreRegisteredUser(userId, phoneNumber)
                if (preRegisteredUser != null) {
                    repository.claimAccount(userId, name)
                    sessionManager.createLoginSession(userId)
                    passwordManager.savePassword(userId, password)
                    _currentLoggedInUser.value = CurrentUserState.Success(preRegisteredUser)
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error("Invalid User ID or phone number combination")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Registration failed: ${e.message}")
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        _currentLoggedInUser.value = CurrentUserState.NotLoggedIn
    }

    //In the future if we have multiple login screens that share the login state, each
    //screen should reset the login state when they are done with it, to avoid bypassing login.
    fun resetLoginState() {
        _loginState.value = LoginState.Initial
    }

    // Login state sealed class
    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    //Register state sealed class
    sealed class RegisterState {
        object Initial : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    sealed class CurrentUserState {
        object Initial : CurrentUserState()
        object Loading : CurrentUserState()
        object NotLoggedIn : CurrentUserState()
        data class Success(val user: Patient) : CurrentUserState()
        data class Error(val message: String) : CurrentUserState()
    }
}