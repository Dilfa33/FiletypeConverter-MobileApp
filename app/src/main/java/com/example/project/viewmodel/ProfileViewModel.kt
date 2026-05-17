package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.di.SessionManager
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import com.example.project.model.User
import com.example.project.repository.FileRepository
import com.example.project.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    object Init : ProfileUiState
    object Loading : ProfileUiState
    data class Success(
        val username: String,
        val email: String,
        val files: List<FileItem>,
        val notificationsEnabled: Boolean = true
    ) : ProfileUiState {
        val totalConversions: Int get() = files.size
        val successRate: Int
            get() = if (files.isEmpty()) 0
                    else (files.count { it.status == ConversionStatus.SUCCESS } * 100) / files.size
        val storageUsedMb: Float get() = files.sumOf { it.sizeMb.toDouble() }.toFloat()
        val recentFiles: List<FileItem> get() = files.take(5)
    }
    data class Error(val message: String) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Init)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadProfile() }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val user = userRepository.getUserById(sessionManager.currentUserId)
                    ?: User(id = "user-1", username = "John Doe", email = "john@example.com")
                fileRepository.getFilesByUser(user.id).collect { files ->
                    _uiState.value = ProfileUiState.Success(
                        username              = user.username,
                        email                 = user.email,
                        files                 = files,
                        notificationsEnabled  = sessionManager.notificationsEnabled
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun updateUsername(newUsername: String) {
        if (newUsername.isBlank()) return
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(sessionManager.currentUserId) ?: return@launch
                userRepository.updateUser(user.copy(username = newUsername))
                val current = _uiState.value as? ProfileUiState.Success ?: return@launch
                _uiState.value = current.copy(username = newUsername)
            } catch (_: Exception) {}
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                fileRepository.deleteAllByUser(sessionManager.currentUserId)
            } catch (_: Exception) {}
        }
    }

    fun toggleNotifications() {
        sessionManager.notificationsEnabled = !sessionManager.notificationsEnabled
        val current = _uiState.value as? ProfileUiState.Success ?: return
        _uiState.value = current.copy(notificationsEnabled = sessionManager.notificationsEnabled)
    }
}
