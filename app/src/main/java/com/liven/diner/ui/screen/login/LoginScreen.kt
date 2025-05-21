package com.liven.diner.ui.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    gotoRegister: () -> Unit = {},
    vm: LoginViewModel = hiltViewModel()
) {

    val email by vm.email.collectAsState()
    val password by vm.password.collectAsState()

    val authState by vm.authenticationState.collectAsState()
    var showLoginForm by remember { mutableStateOf(false) }

    val postLoginResult by vm.postLoginResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            AuthenticationState.Undetermined -> {}
            AuthenticationState.Unauthenticated -> {
                showLoginForm = true
            }

            AuthenticationState.Authenticated -> {
                onLoginSuccess()
            }
        }
    }

    LaunchedEffect(postLoginResult) {
        when (postLoginResult) {
            PostLoginResult.Idle -> {}
            PostLoginResult.Loading -> {}
            is PostLoginResult.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = (postLoginResult as PostLoginResult.Error).message,
                        withDismissAction = true
                    )
                }
            }

            is PostLoginResult.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Success!",
                        withDismissAction = true
                    )
                    onLoginSuccess()
                }
            }
        }
    }

    if (showLoginForm) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Login", style = typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Username Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { vm.onEmailChange(it) },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = postLoginResult is PostLoginResult.Error
                )

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { vm.onPasswordChange(it) },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = postLoginResult is PostLoginResult.Error
                )

                if (postLoginResult is PostLoginResult.Loading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = { vm.postLoginRequest() }
                    ) { Text("Login") }
                }

                Button(onClick = gotoRegister) { Text("Register") }
            }
        }
    }
}