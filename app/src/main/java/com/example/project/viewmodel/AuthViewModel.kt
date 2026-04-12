package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)

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
    val isLoading: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // --- Login ---

    fun onLoginEmailChange(value: String) {
        _loginState.value = _loginState.value.copy(email = value, emailError = null)
    }

    fun onLoginPasswordChange(value: String) {
        _loginState.value = _loginState.value.copy(password = value, passwordError = null)
    }

    fun onLoginPasswordVisibilityToggle() {
        _loginState.value = _loginState.value.copy(
            isPasswordVisible = !_loginState.value.isPasswordVisible
        )
    }

    fun onLoginClick() {
        val state = _loginState.value
        var emailError: String? = null
        var passwordError: String? = null

        if (state.email.isBlank()) emailError = "Email cannot be empty"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches())
            emailError = "Enter a valid email"

        if (state.password.isBlank()) passwordError = "Password cannot be empty"
        else if (state.password.length < 6) passwordError = "Password must be at least 6 characters"

        _loginState.value = state.copy(emailError = emailError, passwordError = passwordError)

        if (emailError == null && passwordError == null) {
            // TODO: implement actual login
        }
    }

    val isLoginEnabled: Boolean
        get() = _loginState.value.email.isNotBlank() && _loginState.value.password.isNotBlank()

    // --- Register ---

    fun onRegisterUsernameChange(value: String) {
        _registerState.value = _registerState.value.copy(username = value, usernameError = null)
    }

    fun onRegisterEmailChange(value: String) {
        _registerState.value = _registerState.value.copy(email = value, emailError = null)
    }

    fun onRegisterPasswordChange(value: String) {
        _registerState.value = _registerState.value.copy(password = value, passwordError = null)
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        _registerState.value = _registerState.value.copy(confirmPassword = value, confirmPasswordError = null)
    }

    fun onRegisterPasswordVisibilityToggle() {
        _registerState.value = _registerState.value.copy(
            isPasswordVisible = !_registerState.value.isPasswordVisible
        )
    }

    fun onRegisterConfirmPasswordVisibilityToggle() {
        _registerState.value = _registerState.value.copy(
            isConfirmPasswordVisible = !_registerState.value.isConfirmPasswordVisible
        )
    }

    fun onRegisterClick() {
        val state = _registerState.value
        var usernameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        if (state.username.isBlank()) usernameError = "Username cannot be empty"
        if (state.email.isBlank()) emailError = "Email cannot be empty"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches())
            emailError = "Enter a valid email"
        if (state.password.isBlank()) passwordError = "Password cannot be empty"
        else if (state.password.length < 6) passwordError = "Password must be at least 6 characters"
        if (state.confirmPassword != state.password) confirmPasswordError = "Passwords do not match"

        _registerState.value = state.copy(
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )

        if (usernameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
            // TODO: implement actual registration
        }
    }

    val isRegisterEnabled: Boolean
        get() = _registerState.value.run {
            username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
        }
}
