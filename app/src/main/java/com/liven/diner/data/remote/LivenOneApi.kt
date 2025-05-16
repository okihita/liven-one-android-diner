package com.liven.diner.data.remote

import com.liven.diner.data.model.auth.LoginRequest
import com.liven.diner.data.model.auth.LoginResponse
import com.liven.diner.data.model.auth.RegisterRequest
import com.liven.diner.data.model.auth.RegisterResponse
import com.liven.diner.data.model.order.MenuItem
import com.liven.diner.data.model.order.Order
import com.liven.diner.data.model.order.OrderRequest
import com.liven.diner.data.model.order.OrderResponse
import com.liven.diner.data.model.venue.VenuesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LivenOneApi {

    // --- Auth ---
    @POST("/auth/register")
    suspend fun postRegister(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("/auth/login")
    suspend fun postLogin(
        @Body request: LoginRequest
    ): LoginResponse

    // --- Venues ---
    @GET("/venues")
    suspend fun getVenues(
        @Query("name") name: String? = null,
        @Query("cuisine_type") cuisineType: String? = null,
    ): VenuesResponse

    @GET("/venues/{venueId}/menu")
    suspend fun getVenueMenu(
        @Path("venueId") venueId: Long
    ): List<MenuItem>

    // --- Orders (Diner) ---
    @POST("/diner/orders")
    suspend fun postOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): OrderResponse

    @GET("/diner/orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): List<Order>
}