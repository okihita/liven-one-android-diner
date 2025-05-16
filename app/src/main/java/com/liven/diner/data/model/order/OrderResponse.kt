package com.liven.diner.data.model.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val order: Order
)
