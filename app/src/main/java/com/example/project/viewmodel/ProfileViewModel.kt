package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val name: String = "John Doe",
    val email: String = "john@example.com",
    val totalConversions: Int = 24,
    val storageUsedMb: Float = 45.2f,
    val successRate: Int = 92
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
