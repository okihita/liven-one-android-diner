package com.liven.diner.data.remote

import com.liven.diner.data.model.LoginRequest
import com.liven.diner.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LivenOneApi {

    @POST("auth/login")
    suspend fun postLogin(
        @Body request: LoginRequest
    ): LoginResponse

}