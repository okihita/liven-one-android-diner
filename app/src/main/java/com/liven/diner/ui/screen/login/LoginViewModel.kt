package com.liven.diner.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.auth.LoginRequest
import com.liven.diner.data.remote.LivenOneApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val livenOneApi: LivenOneApi
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun postLoginRequest() {
        viewModelScope.launch {
            try {
                val loginResponse = livenOneApi.postLogin(LoginRequest(email.value, password.value))
                println(loginResponse)
                _token.value = loginResponse.token
            } catch (e: Exception) {
                _token.value = "Error: ${e.message}"
            }
        }
    }
}