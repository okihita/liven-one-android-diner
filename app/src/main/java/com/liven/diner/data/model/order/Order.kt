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

@Serializable
enum class OrderStatus {

    @SerialName("Pending")
    PENDING,

    @SerialName("Rejected")
    REJECTED,

    @SerialName("Accepted")
    ACCEPTED,

    @SerialName("Cancelled")
    CANCELLED,

    @SerialName("Preparing")
    PREPARING,

    @SerialName("ReadyForPickup")
    READY_FOR_PICKUP,

    @SerialName("Completed")
    COMPLETED;

    companion object {
        fun fromApiValue(value: String?): OrderStatus {
            return when (value) {
                "Pending" -> PENDING
                "Rejected" -> REJECTED
                "Accepted" -> ACCEPTED
                "Cancelled" -> CANCELLED
                "Preparing" -> PREPARING
                "ReadyForPickup" -> READY_FOR_PICKUP
                "Completed" -> COMPLETED
                else -> PENDING
            }
        }
    }
}
