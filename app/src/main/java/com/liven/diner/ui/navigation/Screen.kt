package com.liven.diner.ui.navigation

const val NAV_ARG_VENUE_ID = "venueId"

sealed class Screen(val route: String) {
    object Register : Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")
    object Cart : Screen("cart")

    object VenueDetail : Screen("venueDetail/{$NAV_ARG_VENUE_ID}") {
        fun createRoute(venueId: Long) = "venueDetail/$venueId"
    }
}