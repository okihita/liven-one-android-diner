package com.liven.diner.ui.screen.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.liven.diner.data.model.order.Order
import com.liven.diner.data.model.order.OrderStatus
import com.liven.diner.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Helper to format date strings (optional, or use a proper date/time library)
fun formatOrderTimestamp(timestamp: String): String {
    return try {
        // Assuming timestamp is in ISO 8601 format like "2025-05-23T17:09:37.966222926Z"
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(timestamp)
        date?.let {
            val formatter = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
            formatter.timeZone = TimeZone.getDefault() // Display in local timezone
            formatter.format(it)
        } ?: timestamp
    } catch (e: Exception) {
        timestamp // Return original if parsing fails
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.orderHistoryState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
                navigationIcon = {
                    // Show back button if this screen is not a top-level destination in bottom nav
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
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
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                    // You could add a retry button here
                }

                uiState.orders.isEmpty() -> {
                    Text(
                        text = "You haven't placed any orders yet.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.orders.size) { index ->
                            val order = uiState.orders[index]
                            OrderHistoryItemCard(
                                order = order,
                                onClick = {
                                    navController.navigate(Screen.OrderDetail.createRoute(order.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryItemCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.id}", // Or a more user-friendly order reference
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.status.toString(), // Access the string value from your enum
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (order.status) { // Example of status-based coloring
                        OrderStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                        OrderStatus.CANCELLED, OrderStatus.REJECTED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Venue ID: ${order.venueId}", style = MaterialTheme.typography.bodySmall)

            Text(
                text = "Date: ${formatOrderTimestamp(order.orderTimestamp)}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total: IDR %.0f".format(order.totalAmountInCents / 100.0),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
