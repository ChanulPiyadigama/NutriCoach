package com.fit2081.assignment1ChanulPiyadigama34961496

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Face

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fit2081.assignment1ChanulPiyadigama34961496.ui.theme.Assignment1Theme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.WelcomeScreen
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.LoginScreen
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.FoodQuestionnaireScreen.FoodQuestionnaireScreen
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.HomeScreen
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.InsightsScreen
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.SettingsScreen
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.NutriCoachScreen
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.AdminScreen
import com.fit2081.assignment1ChanulPiyadigama34961496.screens.RegisterScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment1Theme {
                //create the navcontroller to change screens
                val navController = rememberNavController() as NavHostController


                //scaffold passes padding to compoasble (will be contiunusoly passed down to screens)
                //so it is not blocked by system ui (navbar, bottom bar etc)
                //navcontroller will also be passed down
                Scaffold(

                    modifier = Modifier.fillMaxSize(),
                    //based on the destination of the navcontroller, we can show or hide the bottom bar
                    //and get its userid to pass to the bottombar to use for navigation
                    bottomBar = {
                        //we keep the top entry of the backstack (current screen) as state so the bottombar logic is rerun on every navigation
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination?.route

                        // currentdestination is nullable (on app start up route is null since its run first before the navhost default of welcome screen)
                        //therefore ? safely returns null and doesnt run: let function
                        val showBottomBar = currentDestination?.let { route ->
                            route.startsWith("home") ||
                                    route.startsWith("insights") ||
                                    route.startsWith("settings") ||
                                    route.startsWith("nutriCoach")
                        } ?: false

                        if (showBottomBar) {
                            MyBottomAppBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavigationHost(modifier = Modifier.padding(innerPadding), navController = navController)
                }
            }
        }
    }
}

//takes in a navcontroller, defines navigation routes for controller, and passes it for screens to use
@Composable
fun NavigationHost(modifier: Modifier, navController: NavHostController) {
    // Here, the modifier is applied to NavHost directly, compsables (screens) are held within it
    NavHost(navController = navController, startDestination = "welcome", modifier = modifier) {
        Log.d("Navigation", "NavHost is being run")  // Add this
        composable("welcome") {
            WelcomeScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
           RegisterScreen(navController = navController)
        }

        composable("foodQuestionnaire"){
            FoodQuestionnaireScreen(navController= navController)
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("insights"){
            InsightsScreen(navController= navController)
        }


        composable("settings"
        ) {
            SettingsScreen(navController = navController)
        }

        composable("nutriCoach"
        ) {
            NutriCoachScreen(navController = navController)
        }
        composable("admin"
        ) {
            AdminScreen(navController = navController)
        }
    }
}

//bottom bar only has 4 items, so create each navigation bar item individually since some require the userId
//and that would require extra conditionals if we to loop through a list of items, just for 2 items
@Composable
fun MyBottomAppBar(navController: NavHostController) {
    NavigationBar {
        // selected works by accessing the current destination of the navcontroller
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route?.startsWith("home") == true,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Email, contentDescription = "Insights") },
            label = { Text("Insights") },
            selected = navController.currentDestination?.route?.startsWith("insights") == true,
            onClick = { navController.navigate("insights") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = navController.currentDestination?.route?.startsWith("settings") == true,
            onClick = { navController.navigate("settings") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Face, contentDescription = "NutriCoach AI") },
            label = { Text("NutriCoach") },
            selected = navController.currentDestination?.route?.startsWith("nutriCoach") == true,
            onClick = { navController.navigate("nutriCoach") }
        )
    }
}


