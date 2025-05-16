package com.liven.diner.data.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName("ID")
    val id: Long,
    @SerialName("diner_id")
    val dinerId: Long,
    @SerialName("venue_id")
    val venueId: Long,
    @SerialName("order_items")
    val orderItems: List<OrderItem>,
    @SerialName("total_amount_in_cents")
    val totalAmountInCents: Int,
    val status: OrderStatus,
    @SerialName("order_timestamp")
    val orderTimestamp: String
)

enum class OrderStatus(val status: String) {
    PENDING("Pending"),
    REJECTED("Rejected"),
    ACCEPTED("Accepted"),
    CANCELLED("Cancelled"),
    PREPARING("Preparing"),
    READY_FOR_DELIVERY("ReadyForDelivery"),
    COMPLETED("Completed");

    companion object {
        fun fromApiValue(value: String?): OrderStatus? {
            return entries.find { it.status == value } ?: PENDING
        }
    }
}