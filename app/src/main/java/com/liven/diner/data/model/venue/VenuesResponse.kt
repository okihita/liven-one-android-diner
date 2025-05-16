package com.liven.diner.data.model.venue

import kotlinx.serialization.Serializable

@Serializable
data class VenuesResponse(
    val venues: List<Venue>
)