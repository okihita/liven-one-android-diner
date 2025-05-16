package com.liven.diner.data.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    @SerialName("venue_id")
    val venueId: Long,
    @SerialName("items")
    val orderItems: List<OrderItemRequest>
)

@Serializable
data class OrderItemRequest(
    @SerialName("menu_item_id")
    val menuItemId: Long,
    val quantity: Int
)