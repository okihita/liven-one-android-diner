package com.liven.diner.ui.screen.cart

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.liven.diner.data.repository.CartItemUiState
import com.liven.diner.ui.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    vm: CartViewModel = hiltViewModel(),
) {

    val cartItems by vm.cartItems.collectAsStateWithLifecycle()
    val totalPriceCents by vm.totalPriceCents.collectAsStateWithLifecycle()
    val currentVenueId by vm.currentVenueId.collectAsStateWithLifecycle()
    val orderPlacementState by vm.orderPlacementState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(orderPlacementState) {
        when (val state = orderPlacementState) {
            OrderPlacementState.Idle -> {}
            OrderPlacementState.Loading -> {}
            is OrderPlacementState.Error -> {
                Toast.makeText(context, "Error ${state.message}", Toast.LENGTH_SHORT).show()
            }

            is OrderPlacementState.Success -> {
                Toast.makeText(context, "Success. ID: ${state.order.id}", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Your Order") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(shadowElevation = 4.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Venue ID: ${currentVenueId ?: "N/A"}", // Display venue ID
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Total: Rp %.0f".format(totalPriceCents / 100.0),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.End)
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { vm.placeOrder() },
                            enabled = orderPlacementState != OrderPlacementState.Loading && cartItems.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (orderPlacementState == OrderPlacementState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Confirm & Place Order")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty() && orderPlacementState !is OrderPlacementState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty.")
            }
        } else if (orderPlacementState is OrderPlacementState.Loading && cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator() // Show loading if placing order from an empty (just cleared) cart
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartListItem(cartItem = cartItem)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun CartListItem(cartItem: CartItemUiState) {
    ListItem(
        headlineContent = { Text(cartItem.menuItem.name) },
        supportingContent = { Text("Quantity: ${cartItem.quantity}") },
        trailingContent = { Text("Rp %.0f".format((cartItem.menuItem.priceInCents * cartItem.quantity) / 100.0)) }
    )
}


