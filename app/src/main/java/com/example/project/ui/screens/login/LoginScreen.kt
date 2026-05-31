package com.example.project.ui.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project.R
import com.example.project.ui.screens.login.components.LoginButton
import com.example.project.ui.screens.login.components.LoginEmailField
import com.example.project.ui.screens.login.components.LoginPasswordField
import com.example.project.ui.theme.LightBlue
import com.example.project.ui.theme.TextSecondary
import com.example.project.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Google Sign-In client
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { idToken ->
                viewModel.onGoogleSignIn(
                    idToken      = idToken,
                    displayName  = account.displayName,
                    email        = account.email
                )
            }
        } catch (_: ApiException) { /* sign-in cancelled or failed */ }
    }

    LaunchedEffect(state.navigateToHome) {
        if (state.navigateToHome) {
            viewModel.onLoginNavigationHandled()
            onLoginSuccess()
        }
    }

    if (state.showForgotDialog) {
        ForgotPasswordDialog(
            email         = state.forgotEmail,
            error         = state.forgotError,
            onEmailChange = viewModel::onForgotEmailChange,
            onSubmit      = viewModel::onForgotPasswordSubmit,
            onDismiss     = viewModel::onForgotPasswordDismiss
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
                "Password reset email sent! Check your inbox.",
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

        Spacer(modifier = Modifier.height(12.dp))

        // ── OR divider ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                "  OR  ",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Google Sign-In button ─────────────────────────────────────────────
        OutlinedButton(
            onClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text("Continue with Google", fontWeight = FontWeight.SemiBold)
        }

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
    error: String?,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Enter your email — we'll send a reset link.",
                    style = MaterialTheme.typography.bodySmall, color = TextSecondary
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    isError = error != null
                )
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSubmit) { Text("Send Reset Email") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
