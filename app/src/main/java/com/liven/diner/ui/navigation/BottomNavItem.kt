// You can put this in your ui.navigation package or a new ui.common package
package com.liven.diner.ui.navigation // Or your chosen package

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt // Example for Orders
import androidx.compose.material.icons.filled.Home // Example for Home/Venues
import androidx.compose.material.icons.filled.Person // Example for Profile
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class or data class for bottom navigation items
sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Home)
    object OrderHistory : BottomNavItem(Screen.OrderHistory.route, "Orders", Icons.AutoMirrored.Filled.ListAlt)
    // Add more items like Profile if needed
    // object Profile : BottomNavItem(Screen.Profile.route, "Profile", Icons.Filled.Person)
}