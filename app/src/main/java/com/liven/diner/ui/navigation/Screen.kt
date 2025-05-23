package com.liven.diner.ui.navigation

const val NAV_ARG_VENUE_ID = "venueId"
const val NAV_ARG_ORDER_ID = "orderId"

sealed class Screen(val route: String) {
    object Register : Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")

    object VenueDetail : Screen("venueDetail/{$NAV_ARG_VENUE_ID}") {
        fun createRoute(venueId: Long) = "venueDetail/$venueId"
    }

    object Cart : Screen("cart")
    object OrderHistory : Screen("order_history")
    object OrderDetail : Screen("order_detail/{$NAV_ARG_ORDER_ID}") {
        fun createRoute(orderId: Long) = "order_detail/$orderId"
    }

}