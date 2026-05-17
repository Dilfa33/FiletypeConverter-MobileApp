package com.example.project.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsState()

    LaunchedEffect(state.navigateToHome) {
        if (state.navigateToHome) {
            viewModel.onLoginNavigationHandled()
            onLoginSuccess()
        }
    }

    if (state.showForgotDialog) {
        ForgotPasswordDialog(
            email           = state.forgotEmail,
            newPassword     = state.forgotNewPassword,
            error           = state.forgotError,
            onEmailChange   = viewModel::onForgotEmailChange,
            onPasswordChange= viewModel::onForgotNewPasswordChange,
            onSubmit        = viewModel::onForgotPasswordSubmit,
            onDismiss       = viewModel::onForgotPasswordDismiss
        )
    }

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
                fontWeight = FontWeight.Bold, color = LightBlue
            )
        )
        Text(
            text = "Convert anything, anywhere.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        if (state.forgotSuccess) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Password reset successfully!",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text("Welcome back",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.align(Alignment.Start))
        Text("Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start))

        Spacer(modifier = Modifier.height(32.dp))

        LoginEmailField(value = state.email, onValueChange = viewModel::onLoginEmailChange, error = state.emailError)
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
            onClick = viewModel::onForgotPasswordOpen,
            modifier = Modifier.align(Alignment.End)
        ) { Text("Forgot password?", color = LightBlue) }

        Spacer(modifier = Modifier.height(24.dp))

        LoginButton(onClick = viewModel::onLoginClick, enabled = state.isLoginEnabled)

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

@Composable
private fun ForgotPasswordDialog(
    email: String,
    newPassword: String,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enter your email and a new password.",
                    style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    isError = error != null
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onPasswordChange,
                    label = { Text("New password (min 6 chars)") },
                    singleLine = true
                )
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSubmit) { Text("Reset") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
