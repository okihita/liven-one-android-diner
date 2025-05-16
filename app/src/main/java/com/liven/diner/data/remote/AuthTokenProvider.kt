package com.liven.diner.data.remote

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

interface AuthTokenProvider {
    fun loadAuthToken(): String?
    fun saveAuthToken(token: String?)
}

@Singleton
class SharedPreferencesAuthTokenProvider @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AuthTokenProvider {

    private val jwtTokenKey = "jwt_token"

    override fun loadAuthToken(): String? {
        return sharedPreferences.getString(jwtTokenKey, null)
    }

    override fun saveAuthToken(token: String?) {
        sharedPreferences.edit { putString(jwtTokenKey, token) }
    }

    fun clearAuthToken() {
        sharedPreferences.edit { remove(jwtTokenKey) }
    }
}