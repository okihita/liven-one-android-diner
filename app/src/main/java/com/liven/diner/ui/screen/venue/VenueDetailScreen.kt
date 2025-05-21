package com.liven.diner.ui.screen.venue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.liven.diner.data.model.order.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueDetailScreen(
    navController: NavController,
    venueId: Long,
    vm: VenueDetailViewModel = hiltViewModel()
) {
    val getMenuByVenueIdResult by vm.getMenuByVenueIdResult.collectAsState()

    LaunchedEffect(venueId) { vm.getVenueDetail() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Venue Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (getMenuByVenueIdResult) {
                is GetVenueDetailResult.Loading -> CircularProgressIndicator()
                is GetVenueDetailResult.Success -> {

                    val menuItems: List<MenuItem> =
                        (getMenuByVenueIdResult as GetVenueDetailResult.Success).menuItems

                    LazyColumn {
                        items(menuItems.size) { index ->
                            MenuItem(menuItems[index])
                        }
                    }
                }

                is GetVenueDetailResult.Error -> {
                    val error = getMenuByVenueIdResult as GetVenueDetailResult.Error
                    Text("Error: ${error.message}", color = MaterialTheme.colorScheme.error)
                }

                GetVenueDetailResult.Idle -> {}
            }
        }
    }
}

@Composable
fun MenuItem(menuItem: MenuItem, onMenuItemClick: (MenuItem) -> Unit = {}) {
    ListItem(
        headlineContent = { Text(menuItem.name) },
        supportingContent = { Text(menuItem.description) },
        trailingContent = { Text(menuItem.priceInCents.toString()) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onMenuItemClick(menuItem) }
    )
}