package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import com.example.project.model.HardcodedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class StatusFilter { ALL, SUCCESS, FAILED, PROCESSING }

data class HistoryUiState(
    val allFiles: List<FileItem> = HardcodedData.files,
    val searchQuery: String = "",
    val selectedFilter: StatusFilter = StatusFilter.ALL,
    val isLoading: Boolean = false
) {
    // ── Derived / computed states ─────────────────────────────────────────────

    val filteredFiles: List<FileItem>
        get() = allFiles.filter { file ->
            val matchesFilter = selectedFilter == StatusFilter.ALL ||
                    file.status.name == selectedFilter.name
            val matchesSearch = searchQuery.isBlank() ||
                    file.name.contains(searchQuery, ignoreCase = true) ||
                    file.originalFormat.contains(searchQuery, ignoreCase = true) ||
                    file.targetFormat.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }

    val isEmpty: Boolean
        get() = filteredFiles.isEmpty()

    val successCount: Int
        get() = allFiles.count { it.status == ConversionStatus.SUCCESS }

    val failedCount: Int
        get() = allFiles.count { it.status == ConversionStatus.FAILED }

    val totalSizeMb: Float
        get() = allFiles.sumOf { it.sizeMb.toDouble() }.toFloat()
}

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFilterSelected(filter: StatusFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
    }
}
