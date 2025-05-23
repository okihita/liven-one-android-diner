package com.liven.diner.data.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val authTokenProvider: AuthTokenProvider
) {

    private val _navigateToLoginEvent = MutableSharedFlow<Unit>()
    val navigateToLoginEvent = _navigateToLoginEvent.asSharedFlow()

    fun handleSessionExpiration() {
        CoroutineScope(Dispatchers.Main).launch {
            authTokenProvider.clearAuthToken()
            _navigateToLoginEvent.emit(Unit)
        }
    }
}