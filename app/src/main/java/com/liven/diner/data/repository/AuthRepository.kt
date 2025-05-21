package com.liven.diner.data.repository

import com.liven.diner.data.model.auth.LoginRequest
import com.liven.diner.data.model.auth.LoginResponse
import com.liven.diner.data.model.auth.RegisterRequest
import com.liven.diner.data.model.auth.RegisterResponse
import com.liven.diner.data.remote.AuthTokenProvider
import com.liven.diner.data.remote.LivenOneApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    suspend fun postRegister(request: RegisterRequest): Result<RegisterResponse>
    suspend fun postLogin(request: LoginRequest): Result<LoginResponse>
    fun isLoggedIn(): Boolean
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val livenOneApi: LivenOneApi,
    private val authTokenProvider: AuthTokenProvider
) : AuthRepository {

    override suspend fun postRegister(request: RegisterRequest): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = livenOneApi.postRegister(request)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun postLogin(request: LoginRequest): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = livenOneApi.postLogin(request)
                authTokenProvider.saveAuthToken(response.token)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun isLoggedIn(): Boolean {
        return authTokenProvider.loadAuthToken() != null
    }
}