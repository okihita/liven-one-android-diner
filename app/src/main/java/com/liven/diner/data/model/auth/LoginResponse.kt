package com.liven.diner.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String?
)