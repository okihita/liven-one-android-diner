package com.liven.diner.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val message: String?,
    val user: User?
)