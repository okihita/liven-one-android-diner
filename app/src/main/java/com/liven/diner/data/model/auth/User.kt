package com.liven.diner.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    @SerialName("user_type")
    val userType: String
)

enum class UserType(val apiValue: String) {
    DINER("diner"),
    MERCHANT("merchant"),
    UNKNOWN("unknown");

    companion object {
        fun fromApiValue(value: String?): UserType? {
            return entries.find { it.apiValue == value } ?: UNKNOWN
        }
    }
}
