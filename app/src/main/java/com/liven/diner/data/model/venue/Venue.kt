package com.liven.diner.data.model.venue

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Venue(
    @SerialName("ID")
    val id: Long,
    val name: String,
    val address: String,
    val description: String
)
