package com.liven.diner.data.repository

import com.liven.diner.data.model.venue.VenuesResponse
import com.liven.diner.data.remote.LivenOneApi
import javax.inject.Inject
import javax.inject.Singleton

interface VenuesMenuItemsRepository {
    suspend fun getVenues(name: String? = "", cuisineType: String? = ""): Result<VenuesResponse>
    suspend fun getSingleVenueMenus(venueId: Int): Result<VenuesResponse>
}

@Singleton
class VenuesMenuItemsRepositoryImpl @Inject constructor(
    private val livenOneApi: LivenOneApi,
) : VenuesMenuItemsRepository {

    override suspend fun getVenues(name: String?, cuisineType: String?): Result<VenuesResponse> {
        return try {
            val response = livenOneApi.getVenues()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSingleVenueMenus(venueId: Int): Result<VenuesResponse> {
        return Result.failure(Exception("Not implemented"))
    }
}