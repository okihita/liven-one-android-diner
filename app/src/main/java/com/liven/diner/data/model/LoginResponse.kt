package com.liven.diner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)