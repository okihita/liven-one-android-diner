package com.liven.diner.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.liven.diner.ui.navigation.NAV_ARG_VENUE_ID
import com.liven.diner.ui.navigation.Screen
import com.liven.diner.ui.screen.home.HomeScreen
import com.liven.diner.ui.screen.login.LoginScreen
import com.liven.diner.ui.screen.register.RegisterScreen
import com.liven.diner.ui.screen.venue.VenueDetailScreen
import com.liven.diner.ui.theme.DinerExperienceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable(Screen.Home.route) {
                            HomeScreen(onItemClick = { venue ->
                                navController.navigate(Screen.VenueDetail.createRoute(venue.id))
                            })
                        }
                        composable(
                            route = Screen.VenueDetail.route,
                            arguments = listOf(navArgument(NAV_ARG_VENUE_ID) {
                                type = NavType.LongType
                            })
                        ) {
                            val venueId = it.arguments?.getLong(NAV_ARG_VENUE_ID)
                            if (venueId != null) VenueDetailScreen(navController)
                            else Text("Error: Venue ID not found.")
                        }
                    }
                }
            }
        }
    }
}
