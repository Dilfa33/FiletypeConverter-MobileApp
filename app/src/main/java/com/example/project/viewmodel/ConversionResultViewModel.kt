package com.example.project.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.model.FileItem
import com.example.project.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ConversionResultUiState {
    object Loading : ConversionResultUiState
    data class Success(val file: FileItem) : ConversionResultUiState
    data class Error(val message: String) : ConversionResultUiState
}

@HiltViewModel
class ConversionResultViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val fileId: String = savedStateHandle["fileId"] ?: ""

    private val _uiState = MutableStateFlow<ConversionResultUiState>(ConversionResultUiState.Loading)
    val uiState: StateFlow<ConversionResultUiState> = _uiState.asStateFlow()

    init {
        loadFile()
    }

    private fun loadFile() {
        viewModelScope.launch {
            try {
                val file = fileRepository.getFileById(fileId)
                _uiState.value = if (file != null) ConversionResultUiState.Success(file)
                                 else ConversionResultUiState.Error("File not found")
            } catch (e: Exception) {
                _uiState.value = ConversionResultUiState.Error(e.message ?: "Failed to load result")
            }
        }
    }
}
