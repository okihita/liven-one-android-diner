package com.liven.diner.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    val getVenuesResult by vm.venuesResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Venues") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (getVenuesResult) {
                GetVenuesResult.Idle -> {
                }
                GetVenuesResult.Loading -> {}
                is GetVenuesResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Error: ${(getVenuesResult as GetVenuesResult.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        // Optionally add a retry button
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
                                Text(venue.name)
                            }
                        }
                    }
                }
            }
        }
    }
}