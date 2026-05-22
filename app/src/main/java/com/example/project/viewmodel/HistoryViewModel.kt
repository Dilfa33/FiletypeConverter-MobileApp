package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.di.SessionManager
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import com.example.project.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class StatusFilter { ALL, SUCCESS, FAILED, PROCESSING }

sealed interface HistoryUiState {
    object Init : HistoryUiState
    object Loading : HistoryUiState
    data class Success(
        val files: List<FileItem>,
        val searchQuery: String = "",
        val selectedFilter: StatusFilter = StatusFilter.ALL
    ) : HistoryUiState {
        val filteredFiles: List<FileItem>
            get() = files.filter { file ->
                val matchesFilter = selectedFilter == StatusFilter.ALL ||
                        file.status.name == selectedFilter.name
                val matchesSearch = searchQuery.isBlank() ||
                        file.name.contains(searchQuery, ignoreCase = true) ||
                        file.originalFormat.contains(searchQuery, ignoreCase = true) ||
                        file.targetFormat.contains(searchQuery, ignoreCase = true)
                matchesFilter && matchesSearch
            }
        val isEmpty: Boolean get() = filteredFiles.isEmpty()
        val successCount: Int get() = files.count { it.status == ConversionStatus.SUCCESS }
        val failedCount: Int get() = files.count { it.status == ConversionStatus.FAILED }
        val totalSizeMb: Float get() = files.sumOf { it.sizeMb.toDouble() }.toFloat()
    }
    data class Error(val message: String) : HistoryUiState
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Init)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadFiles()
    }

    private fun loadFiles() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            try {
                fileRepository.getFilesByUser(sessionManager.currentUserId).collect { files ->
                    val current = _uiState.value as? HistoryUiState.Success
                    _uiState.value = HistoryUiState.Success(
                        files = files,
                        searchQuery = current?.searchQuery ?: "",
                        selectedFilter = current?.selectedFilter ?: StatusFilter.ALL
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Failed to load history")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val current = _uiState.value as? HistoryUiState.Success ?: return
        _uiState.value = current.copy(searchQuery = query)
    }

    fun onFilterSelected(filter: StatusFilter) {
        val current = _uiState.value as? HistoryUiState.Success ?: return
        _uiState.value = current.copy(selectedFilter = filter)
    }

    fun clearSearch() {
        val current = _uiState.value as? HistoryUiState.Success ?: return
        _uiState.value = current.copy(searchQuery = "")
    }
}
