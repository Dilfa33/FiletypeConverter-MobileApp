package com.example.project.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project.ui.screens.register.components.RegisterButton
import com.example.project.ui.screens.register.components.RegisterEmailField
import com.example.project.ui.screens.register.components.RegisterPasswordField
import com.example.project.ui.screens.register.components.RegisterUsernameField
import com.example.project.ui.theme.LightBlue
import com.example.project.ui.theme.TextSecondary
import com.example.project.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registerState.collectAsState()

    // UI reacts to state change — navigation is triggered by ViewModel, not by button click
    LaunchedEffect(state.navigateToHome) {
        if (state.navigateToHome) {
            viewModel.onRegisterNavigationHandled()
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

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

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Create account",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Join to start converting files",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        RegisterUsernameField(
            value = state.username,
            onValueChange = viewModel::onRegisterUsernameChange,
            error = state.usernameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterEmailField(
            value = state.email,
            onValueChange = viewModel::onRegisterEmailChange,
            error = state.emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterPasswordField(
            value = state.password,
            onValueChange = viewModel::onRegisterPasswordChange,
            isVisible = state.isPasswordVisible,
            onVisibilityToggle = viewModel::onRegisterPasswordVisibilityToggle,
            error = state.passwordError,
            label = "Password"
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterPasswordField(
            value = state.confirmPassword,
            onValueChange = viewModel::onRegisterConfirmPasswordChange,
            isVisible = state.isConfirmPasswordVisible,
            onVisibilityToggle = viewModel::onRegisterConfirmPasswordVisibilityToggle,
            error = state.confirmPasswordError,
            label = "Confirm Password"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Button only triggers ViewModel — ViewModel decides navigation via state
        RegisterButton(
            onClick = viewModel::onRegisterClick,
            enabled = state.isRegisterEnabled
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?", color = TextSecondary)
            TextButton(onClick = onNavigateToLogin) {
                Text("Sign in", color = LightBlue, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
