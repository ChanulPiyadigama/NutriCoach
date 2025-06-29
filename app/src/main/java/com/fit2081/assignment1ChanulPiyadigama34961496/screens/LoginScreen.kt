package com.fit2081.assignment1ChanulPiyadigama34961496.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.AuthViewModel.LoginState
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.FoodIntakeViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.viewModels.PatientViewModel
import com.fit2081.assignment1ChanulPiyadigama34961496.utils.CustomDropdown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val loginState by authViewModel.loginState.collectAsState()


    val foodIntakeViewModel: FoodIntakeViewModel = viewModel()

    //state for patient IDs
    val patientViewModel: PatientViewModel = viewModel()
    val patientIds = patientViewModel.patientIds.collectAsState(initial = emptyList())


    var selectedUserId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Handle authentication state changes
    when (val state = loginState) {
        is LoginState.Loading -> {
            CircularProgressIndicator()
        }
        is LoginState.Success -> {
            LaunchedEffect(Unit) {
                foodIntakeViewModel.validateFoodIntake(selectedUserId) { isComplete ->
                    if (isComplete) {
                        navController.navigate("home")
                    } else {
                        navController.navigate("foodQuestionnaire")
                    }
                }
            }
            // Reset login state after successful login, so other login screens wont bypass login
            authViewModel.resetLoginState()
        }
        is LoginState.Error -> {
            errorMessage = state.message
        }
        else -> {  }
    }

    // Example of a simple LoginScreen
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Login", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp))

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdown(
            label = "Select your ID (provided by clinician)",
            selectedValue = selectedUserId,
            options = patientIds.value,
            onValueSelected = { selectedUserId = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("This app is for pre-registered users only. Please have your ID and password ready.")

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(fontSize = 14.sp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (selectedUserId.isEmpty() || password.isEmpty()) {
                        errorMessage = "Please provide a user ID and password"
                    } else {
                        authViewModel.login(selectedUserId, password)
                    }
                }
            ) {
                Text("Login")
            }

            Button(
                onClick = {
                    navController.navigate("register")
                }
            ) {
                Text("Register")
            }
        }
    }
}