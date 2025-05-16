package com.liven.diner.ui.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.auth.RegisterRequest
import com.liven.diner.data.model.auth.UserType
import com.liven.diner.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostRegisterResult {
    object Idle : PostRegisterResult
    object Loading : PostRegisterResult
    data class Success(val message: String) : PostRegisterResult
    data class Error(val message: String) : PostRegisterResult
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _postRegisterResult = MutableStateFlow<PostRegisterResult>(PostRegisterResult.Idle)
    val postRegisterResult: StateFlow<PostRegisterResult> = _postRegisterResult.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun postRegisterRequest() {

        _postRegisterResult.value = PostRegisterResult.Loading

        // Set loading screen
        // Avoid double sending request
        // Launch API request

        viewModelScope.launch {

            val postLoginResult = authRepo.postRegister(
                RegisterRequest(email.value, password.value, UserType.DINER.apiValue)
            )

            postLoginResult
                .onSuccess {
                    _postRegisterResult.value = PostRegisterResult.Success(it.message.toString())
                }
                .onFailure {
                    _postRegisterResult.value = PostRegisterResult.Error(it.message.toString())
                }
        }
    }

    fun onRegistrationAttemptHandled() {
        _postRegisterResult.value = PostRegisterResult.Idle
    }
}