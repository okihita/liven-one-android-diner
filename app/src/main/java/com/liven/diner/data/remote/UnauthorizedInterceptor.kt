package com.liven.diner.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnauthorizedInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) sessionManager.handleSessionExpiration()

        return response
    }
}