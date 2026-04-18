package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val navigateToHome: Boolean = false
) {
    // Derived state — recomposition triggers correctly because it lives inside the state class
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val navigateToHome: Boolean = false
) {
    // Derived state — same principle
    val isRegisterEnabled: Boolean
        get() = username.isNotBlank() && email.isNotBlank() &&
                password.isNotBlank() && confirmPassword.isNotBlank()
}

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // ── Login ─────────────────────────────────────────────────────────────────

    fun onLoginEmailChange(value: String) {
        _loginState.update { it.copy(email = value, emailError = null) }
    }

    fun onLoginPasswordChange(value: String) {
        _loginState.update { it.copy(password = value, passwordError = null) }
    }

    fun onLoginPasswordVisibilityToggle() {
        _loginState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClick() {
        val state = _loginState.value
        var emailError: String? = null
        var passwordError: String? = null

        if (state.email.isBlank())
            emailError = "Email cannot be empty"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches())
            emailError = "Enter a valid email"

        if (state.password.isBlank())
            passwordError = "Password cannot be empty"
        else if (state.password.length < 6)
            passwordError = "Password must be at least 6 characters"

        // ViewModel decides whether to navigate — not the UI
        val success = emailError == null && passwordError == null
        _loginState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                navigateToHome = success
            )
        }
    }

    fun onLoginNavigationHandled() {
        _loginState.update { it.copy(navigateToHome = false) }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun onRegisterUsernameChange(value: String) {
        _registerState.update { it.copy(username = value, usernameError = null) }
    }

    fun onRegisterEmailChange(value: String) {
        _registerState.update { it.copy(email = value, emailError = null) }
    }

    fun onRegisterPasswordChange(value: String) {
        _registerState.update { it.copy(password = value, passwordError = null) }
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        _registerState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onRegisterPasswordVisibilityToggle() {
        _registerState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRegisterConfirmPasswordVisibilityToggle() {
        _registerState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onRegisterClick() {
        val state = _registerState.value
        var usernameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        if (state.username.isBlank())
            usernameError = "Username cannot be empty"
        if (state.email.isBlank())
            emailError = "Email cannot be empty"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches())
            emailError = "Enter a valid email"
        if (state.password.isBlank())
            passwordError = "Password cannot be empty"
        else if (state.password.length < 6)
            passwordError = "Password must be at least 6 characters"
        if (state.confirmPassword != state.password)
            confirmPasswordError = "Passwords do not match"

        val success = usernameError == null && emailError == null &&
                passwordError == null && confirmPasswordError == null

        _registerState.update {
            it.copy(
                usernameError = usernameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                navigateToHome = success
            )
        }
    }

    fun onRegisterNavigationHandled() {
        _registerState.update { it.copy(navigateToHome = false) }
    }
}
