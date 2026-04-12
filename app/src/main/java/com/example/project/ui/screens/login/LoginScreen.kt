package com.example.project.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.ui.screens.login.components.LoginButton
import com.example.project.ui.screens.login.components.LoginEmailField
import com.example.project.ui.screens.login.components.LoginPasswordField
import com.example.project.ui.theme.LightBlue
import com.example.project.ui.theme.TextSecondary
import com.example.project.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "FileCast",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = LightBlue
            )
        )

        Text(
            text = "Convert anything, anywhere.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.align(Alignment.Start)
        )

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        LoginEmailField(
            value = state.email,
            onValueChange = viewModel::onLoginEmailChange,
            error = state.emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginPasswordField(
            value = state.password,
            onValueChange = viewModel::onLoginPasswordChange,
            isVisible = state.isPasswordVisible,
            onVisibilityToggle = viewModel::onLoginPasswordVisibilityToggle,
            error = state.passwordError
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { /* TODO: forgot password */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot password?", color = LightBlue)
        }

        Spacer(modifier = Modifier.height(24.dp))

        LoginButton(
            onClick = {
                viewModel.onLoginClick()
                // TODO: navigate on success once real auth is wired up
                onLoginSuccess()
            },
            enabled = viewModel.isLoginEnabled
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account?", color = TextSecondary)
            TextButton(onClick = onNavigateToRegister) {
                Text("Sign up", color = LightBlue, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
