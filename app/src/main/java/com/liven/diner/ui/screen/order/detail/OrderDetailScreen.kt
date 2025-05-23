package com.liven.diner.ui.screen.order.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.liven.diner.data.model.order.Order // Your Order DTO
import com.liven.diner.data.model.order.OrderItem // Your OrderItem DTO
import com.liven.diner.ui.screen.order.formatOrderTimestamp // Reuse your timestamp formatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                uiState.order == null -> { // Should ideally not happen if loading finishes and error is null
                    Text(
                        text = "Order details not available.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else -> {
                    val order = uiState.order!! // Safe because we checked for null
                    OrderDetailContent(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderDetailContent(order: Order) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("Order #${order.id}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Status: ${order.status}", style = MaterialTheme.typography.titleMedium) // Access string value from enum
            Text("Date: ${formatOrderTimestamp(order.orderTimestamp)}", style = MaterialTheme.typography.bodyMedium)
            // Assuming your Order DTO from backend includes nested Venue information
            // If not, you'd just display venueId or fetch venue details separately (more complex)
            Text("Venue ID: ${order.venueId}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Items Ordered", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(order.orderItems) { orderItem ->
            OrderItemDetailRow(orderItem = orderItem)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text("Total Amount: ", style = MaterialTheme.typography.titleMedium)
                Text(
                    "IDR %.0f".format(order.totalAmountInCents / 100.0),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // You can add diner details if available in order.diner and needed
            // order.diner?.let {
            //    Text("Ordered by: ${it.email}", style = MaterialTheme.typography.bodySmall)
            // }
        }
    }
}

@Composable
fun OrderItemDetailRow(orderItem: OrderItem) {
    // Assuming OrderItem DTO has nested MenuItem information or enough details
    // If orderItem.menuItem is available from preloading in backend:
    // val itemName = orderItem.menuItem?.name ?: "Unknown Item"
    // val itemDescription = orderItem.menuItem?.description ?: ""
    // For now, we'll rely on fields directly in OrderItem if MenuItem is not nested or use MenuItemID

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            // If you have menuItem.name directly or need to fetch it:
            // For now, just show ID as a placeholder if name isn't directly available
            text = "Item ID: ${orderItem.menuItemId} (Name would go here)", // Replace with actual name if available
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        // if (itemDescription.isNotBlank()) {
        //     Text(itemDescription, style = MaterialTheme.typography.bodySmall)
        // }
        Text(
            "Quantity: ${orderItem.quantity}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "Price Each: IDR %.0f".format(orderItem.priceInCentsAtOrder / 100.0), // Assuming priceAtOrder is in cents
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "Subtotal: IDR %.0f".format((orderItem.priceInCentsAtOrder * orderItem.quantity) / 100.0),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}