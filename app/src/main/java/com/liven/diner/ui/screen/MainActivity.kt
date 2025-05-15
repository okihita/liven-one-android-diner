package com.liven.diner.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liven.diner.ui.navigation.Screen
import com.liven.diner.ui.theme.DinerExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding ->

                DinerExperienceTheme {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                gotoRegister = {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }
                        composable(Screen.Register.route) {
                            RegisterScreen {}
                        }
                        composable(Screen.Home.route) {
                            Text("Home Screen")
                        }
                    }
                }
            }
        }
    }
}
