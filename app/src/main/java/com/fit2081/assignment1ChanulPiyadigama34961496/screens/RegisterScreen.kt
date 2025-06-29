package com.fit2081.assignment1ChanulPiyadigama34961496.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.AuthViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.AuthViewModel.RegisterState
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.PatientViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.utils.CustomDropdown
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    //to track register state for ui changes
    val authViewModel: AuthViewModel = viewModel()
    val registerState by authViewModel.registerState.collectAsState()

    //To grab patient IDs for the dropdown
    val patientViewModel: PatientViewModel = viewModel()
    val patientIds = patientViewModel.patientIds.collectAsState(initial = emptyList())



    var selectedUserId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }


    var phoneNumberError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }



    when (val state = registerState) {
        is RegisterState.Loading -> {
            CircularProgressIndicator()
        }
        is RegisterState.Success -> {
            LaunchedEffect(Unit) {
                navController.navigate("foodQuestionnaire")
            }
        }
        is RegisterState.Error -> {
            errorMessage = state.message
        }
        else -> {}
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Register Account", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp))

        Spacer(modifier = Modifier.height(16.dp))


        CustomDropdown(
            label = "Select your ID (provided by clinician)",
            selectedValue = selectedUserId,
            options = patientIds.value,
            onValueSelected = { selectedUserId = it }
        )


        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { newValue ->
                // Only accept digits
                if (newValue.all { it.isDigit() }) {
                    phoneNumber = newValue
                    // Validate length as user types
                    phoneNumberError = when {
                        newValue.isEmpty() -> ""
                        newValue.length != 11 -> "Phone number must be 11 digits"
                        else -> ""
                    }
                }
            },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            //highlights the textField when there's an error
            isError = phoneNumberError.isNotEmpty(),
            //displays error message under when the phone number is the wrong length
            supportingText = {
                if (phoneNumberError.isNotEmpty()) {
                    Text(
                        text = phoneNumberError,
                        color = Color.Red
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { newValue ->
                // Only accept letters and spaces
                if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                    name = newValue
                }
            },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { newValue ->
                password = newValue
                passwordError = when {
                    newValue.isEmpty() -> ""
                    newValue.length < 8 -> "Password must be 8 digits or more"
                    else -> ""
                }
            },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError.isNotEmpty(),
            supportingText = {
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = Color.Red
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { newValue ->
                confirmPassword = newValue
                confirmPasswordError = when {
                    newValue.isEmpty() -> ""
                    newValue != password -> "Passwords do not match"
                    else -> ""
                }
            },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = confirmPasswordError.isNotEmpty(),
            supportingText = {
                if (confirmPasswordError.isNotEmpty()) {
                    Text(
                        text = confirmPasswordError,
                        color = Color.Red
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        Button(
            onClick = {
                // Form validation
                when {
                    selectedUserId.isEmpty() -> errorMessage = "Please select a user ID"
                    name.isEmpty() -> errorMessage = "Please enter your name"
                    phoneNumber.isEmpty() -> errorMessage = "Please enter your phone number"
                    phoneNumberError.isNotEmpty() -> errorMessage = "Phone number required "
                    password.isEmpty() -> errorMessage = "Please enter a password"
                    passwordError.isNotEmpty() -> errorMessage = "Password required"
                    password != confirmPassword -> errorMessage = "Passwords do not match"
                    else -> {
                        authViewModel.register(selectedUserId, phoneNumber, name, password)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ){
            Text("Back to Login")
        }

    }
}