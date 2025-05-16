package com.liven.diner.data.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OrderItem(
    @SerialName("ID")
    val id: Long,
    @SerialName("menu_item_id")
    val menuItemId: Long,
    val quantity: Int,
    @SerialName("price_in_cents_at_order")
    val priceInCentsAtOrder: Int,
)