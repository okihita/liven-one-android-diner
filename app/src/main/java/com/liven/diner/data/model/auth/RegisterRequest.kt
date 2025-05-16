package com.liven.diner.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("user_type")
    val userType: String = "diner"
)