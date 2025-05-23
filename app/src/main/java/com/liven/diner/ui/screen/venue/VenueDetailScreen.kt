package com.liven.diner.ui.screen.venue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.liven.diner.data.model.order.MenuItem
import com.liven.diner.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueDetailScreen(
    navController: NavController,
    vm: VenueDetailViewModel = hiltViewModel()
) {
    val screenState by vm.screenState.collectAsState()
    val cartItems by vm.cartItemsUiState.collectAsState()
    val totalPriceCents by vm.totalPriceCents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Venue Menu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (cartItems.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Screen.Cart.route) },
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
                    text = { Text("View Cart (IDR %.2f)".format(totalPriceCents / 100.0)) }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (screenState.isLoadingMenu) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (screenState.error != null) {
                Text("Error: ${screenState.error}", color = MaterialTheme.colorScheme.error)
            } else if (screenState.menuItems.isEmpty()) {
                Text("No menu items found")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(screenState.menuItems.size) { index ->
                        val menuItem = screenState.menuItems[index]

                        val cartItem = cartItems.find { it.menuItem.id == menuItem.id }
                        val quantity = cartItem?.quantity ?: 0

                        MenuItemRow(
                            menuItem = menuItem,
                            quantity = quantity,
                            onIncrement = { vm.incrementItemQty(menuItem.id) },
                            onDecrement = { vm.decrementItemQty(menuItem.id) }
                        )
                        if (index < screenState.menuItems.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemRow(
    menuItem: MenuItem,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) = ListItem(
    headlineContent = { Text(menuItem.name, style = MaterialTheme.typography.titleMedium) },
    supportingContent = {
        if (menuItem.description.isNotBlank()) {
            Text(menuItem.description, style = MaterialTheme.typography.bodySmall)
        }
    },
    leadingContent = {
        // Optional: Display an image if you add it to your MenuItem model
        // Image(painter = rememberAsyncImagePainter(model = menuItem.imageUrl), contentDescription = menuItem.name)
    },
    trailingContent = {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "USD %.2f".format(menuItem.priceInCents / 100.0), // Basic formatting
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrement, enabled = quantity > 0) {
                    Icon(
                        Icons.Filled.RemoveCircleOutline,
                        contentDescription = "Decrement quantity"
                    )
                }
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                )
                IconButton(onClick = onIncrement) {
                    Icon(
                        Icons.Filled.AddCircleOutline,
                        contentDescription = "Increment quantity"
                    )
                }
            }
        }
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp) // Removed horizontal padding to align with typical ListItem
)
