package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import com.example.project.model.HardcodedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val name: String = "John Doe",
    val email: String = "john@example.com",
    val allFiles: List<FileItem> = HardcodedData.files
) {
    // ── Derived / computed states ─────────────────────────────────────────────

    val totalConversions: Int
        get() = allFiles.size

    val successRate: Int
        get() = if (allFiles.isEmpty()) 0
                else (allFiles.count { it.status == ConversionStatus.SUCCESS } * 100) / allFiles.size

    val storageUsedMb: Float
        get() = allFiles.sumOf { it.sizeMb.toDouble() }.toFloat()

    val recentFiles: List<FileItem>
        get() = allFiles.take(5)
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
