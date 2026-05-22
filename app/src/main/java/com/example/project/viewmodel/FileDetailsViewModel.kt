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

sealed interface FileDetailsUiState {
    object Init : FileDetailsUiState
    object Loading : FileDetailsUiState
    data class Success(val file: FileItem) : FileDetailsUiState
    data class Error(val message: String) : FileDetailsUiState
}

@HiltViewModel
class FileDetailsViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val fileId: String = savedStateHandle["fileId"] ?: ""

    private val _uiState = MutableStateFlow<FileDetailsUiState>(FileDetailsUiState.Init)
    val uiState: StateFlow<FileDetailsUiState> = _uiState.asStateFlow()

    init {
        loadFile()
    }

    private fun loadFile() {
        viewModelScope.launch {
            _uiState.value = FileDetailsUiState.Loading
            try {
                val file = fileRepository.getFileById(fileId)
                _uiState.value = if (file != null) FileDetailsUiState.Success(file)
                                 else FileDetailsUiState.Error("File not found")
            } catch (e: Exception) {
                _uiState.value = FileDetailsUiState.Error(e.message ?: "Failed to load file")
            }
        }
    }
}
