package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.local.util.sha256
import com.example.project.di.SessionManager
import com.example.project.model.User
import com.example.project.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val navigateToHome: Boolean = false,
    val showForgotDialog: Boolean = false,
    val forgotEmail: String = "",
    val forgotNewPassword: String = "",
    val forgotError: String? = null,
    val forgotSuccess: Boolean = false
) {
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
    val isRegisterEnabled: Boolean
        get() = username.isNotBlank() && email.isNotBlank() &&
                password.isNotBlank() && confirmPassword.isNotBlank()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // ── Login ─────────────────────────────────────────────────────────────────

    fun onLoginEmailChange(value: String) =
        _loginState.update { it.copy(email = value, emailError = null) }

    fun onLoginPasswordChange(value: String) =
        _loginState.update { it.copy(password = value, passwordError = null) }

    fun onLoginPasswordVisibilityToggle() =
        _loginState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

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

        if (emailError != null || passwordError != null) {
            _loginState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true) }
            try {
                val users = userRepository.getAllUsers().first()
                val user = users.find { it.email == state.email }
                when {
                    user == null ->
                        _loginState.update { it.copy(isLoading = false, emailError = "No account found with this email") }
                    user.passwordHash != state.password.sha256() ->
                        _loginState.update { it.copy(isLoading = false, passwordError = "Incorrect password") }
                    else -> {
                        sessionManager.currentUserId = user.id
                        _loginState.update { it.copy(isLoading = false, navigateToHome = true) }
                    }
                }
            } catch (e: Exception) {
                _loginState.update { it.copy(isLoading = false, emailError = "Login failed, please try again") }
            }
        }
    }

    fun onLoginNavigationHandled() =
        _loginState.update { it.copy(navigateToHome = false) }

    // ── Forgot password ───────────────────────────────────────────────────────

    fun onForgotPasswordOpen() =
        _loginState.update { it.copy(showForgotDialog = true, forgotError = null, forgotSuccess = false) }

    fun onForgotPasswordDismiss() =
        _loginState.update { it.copy(showForgotDialog = false, forgotEmail = "", forgotNewPassword = "", forgotError = null) }

    fun onForgotEmailChange(value: String) =
        _loginState.update { it.copy(forgotEmail = value, forgotError = null) }

    fun onForgotNewPasswordChange(value: String) =
        _loginState.update { it.copy(forgotNewPassword = value) }

    fun onForgotPasswordSubmit() {
        val state = _loginState.value
        if (state.forgotEmail.isBlank() || state.forgotNewPassword.length < 6) {
            _loginState.update { it.copy(forgotError = "Enter a valid email and a password (min 6 chars)") }
            return
        }
        viewModelScope.launch {
            try {
                val users = userRepository.getAllUsers().first()
                val user = users.find { it.email == state.forgotEmail }
                if (user == null) {
                    _loginState.update { it.copy(forgotError = "No account found with this email") }
                } else {
                    userRepository.updateUser(user.copy(passwordHash = state.forgotNewPassword.sha256()))
                    _loginState.update { it.copy(showForgotDialog = false, forgotEmail = "", forgotNewPassword = "", forgotSuccess = true) }
                }
            } catch (e: Exception) {
                _loginState.update { it.copy(forgotError = "Failed to reset password") }
            }
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun onRegisterUsernameChange(value: String) =
        _registerState.update { it.copy(username = value, usernameError = null) }

    fun onRegisterEmailChange(value: String) =
        _registerState.update { it.copy(email = value, emailError = null) }

    fun onRegisterPasswordChange(value: String) =
        _registerState.update { it.copy(password = value, passwordError = null) }

    fun onRegisterConfirmPasswordChange(value: String) =
        _registerState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }

    fun onRegisterPasswordVisibilityToggle() =
        _registerState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun onRegisterConfirmPasswordVisibilityToggle() =
        _registerState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }

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

        if (usernameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            _registerState.update {
                it.copy(usernameError = usernameError, emailError = emailError,
                    passwordError = passwordError, confirmPasswordError = confirmPasswordError)
            }
            return
        }

        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true) }
            try {
                val newUser = User(
                    id           = UUID.randomUUID().toString(),
                    username     = state.username,
                    email        = state.email,
                    passwordHash = state.password.sha256()
                )
                userRepository.insertUser(newUser)
                sessionManager.currentUserId = newUser.id
                _registerState.update { it.copy(isLoading = false, navigateToHome = true) }
            } catch (e: Exception) {
                _registerState.update { it.copy(isLoading = false, emailError = "Registration failed, please try again") }
            }
        }
    }

    fun onRegisterNavigationHandled() =
        _registerState.update { it.copy(navigateToHome = false) }
}
