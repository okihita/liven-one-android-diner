package com.liven.diner.ui.screen.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    vm: RegisterViewModel = hiltViewModel()
) {

    val email by vm.email.collectAsState()
    val password by vm.password.collectAsState()

    val registerResult by vm.postRegisterResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(registerResult) {
        when (registerResult) {
            PostRegisterResult.Idle -> {}
            PostRegisterResult.Loading -> {}
            is PostRegisterResult.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = (registerResult as PostRegisterResult.Error).message,
                        withDismissAction = true
                    )
                }
            }

            is PostRegisterResult.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = (registerResult as PostRegisterResult.Success).message,
                        duration = SnackbarDuration.Short
                    )
                    onRegisterSuccess()
                }
            }
        }
    }

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
            Text("Register", style = typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Username Field
            OutlinedTextField(
                value = email,
                onValueChange = { vm.onEmailChange(it) },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = registerResult is PostRegisterResult.Error
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { vm.onPasswordChange(it) },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = registerResult is PostRegisterResult.Error
            )

            if (registerResult is PostRegisterResult.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { vm.postRegisterRequest() },
                ) { Text("Register as Diner") }
            }

            if (registerResult is PostRegisterResult.Error && snackbarHostState.currentSnackbarData == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (registerResult as PostRegisterResult.Error).message,
                    color = colorScheme.error,
                    style = typography.bodySmall
                )
            }
        }
    }
}