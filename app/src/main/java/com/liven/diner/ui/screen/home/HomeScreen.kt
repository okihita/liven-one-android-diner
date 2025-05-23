package com.liven.diner.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.liven.diner.data.model.venue.Venue
import com.liven.diner.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    vm: HomeViewModel = hiltViewModel(),
    onItemClick: (Venue) -> Unit
) {
    val getVenuesResult by vm.venuesResult.collectAsState()

    LaunchedEffect(Unit) { // key1 = Unit ensures this runs once when composable enters composition
        vm.navigationEvent.collect { event ->
            when (event) {
                is HomeViewModel.NavigationEvent.NavigateToLogin -> {
                    navController.navigate(Screen.Login.route) {
                        // Clear the entire back stack up to the start destination of the graph
                        // and popUpTo the Home screen itself to remove it.
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        // Or if Home is the start of a "logged in" graph:
                        // popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true // Avoid multiple login screens
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Venues") },
                actions = {
                    IconButton(onClick = { vm.logoutUser() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (getVenuesResult) {
                GetVenuesResult.Idle -> {}
                GetVenuesResult.Loading -> {}
                is GetVenuesResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Error: ${(getVenuesResult as GetVenuesResult.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is GetVenuesResult.Success -> {
                    val venues = (getVenuesResult as GetVenuesResult.Success).venues
                    if (venues.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No venues found")
                        }
                    } else {
                        LazyColumn {
                            items(venues.size) { index ->
                                val venue = venues[index]
                                Venue(venue, onItemClick = onItemClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Venue(venue: Venue, onItemClick: (Venue) -> Unit) {
    ListItem(
        headlineContent = { Text(venue.name) },
        supportingContent = { Text(venue.description) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onItemClick(venue) }
    )
}