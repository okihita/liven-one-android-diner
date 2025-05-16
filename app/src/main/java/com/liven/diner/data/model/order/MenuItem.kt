package com.liven.diner.data.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
    @SerialName("ID")
    val id: Long,
    val name: String,
    val description: String,
    @SerialName("price_in_cents")
    val priceInCents: Int,
    val category: String,
    @SerialName("venue_id")
    val venueId: Long
)