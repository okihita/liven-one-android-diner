package com.liven.diner.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.auth.LoginRequest
import com.liven.diner.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostLoginResult {
    object Idle : PostLoginResult
    object Loading : PostLoginResult
    data class Success(val token: String) : PostLoginResult
    data class Error(val message: String) : PostLoginResult
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _postLoginResult = MutableStateFlow<PostLoginResult>(PostLoginResult.Idle)
    val postLoginResult: StateFlow<PostLoginResult> = _postLoginResult.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun postLoginRequest() {

        _postLoginResult.value = PostLoginResult.Loading

        // Set loading screen
        // Avoid double sending request
        // Launch API request

        viewModelScope.launch {

            val postLoginResult = authRepo.postLogin(LoginRequest(email.value, password.value))
            postLoginResult.onSuccess {
                    _postLoginResult.value = PostLoginResult.Success("")
                }.onFailure {
                    _postLoginResult.value = PostLoginResult.Error(it.message.toString())
                }
        }
    }
}