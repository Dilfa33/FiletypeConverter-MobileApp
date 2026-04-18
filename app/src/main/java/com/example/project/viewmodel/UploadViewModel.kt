package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import com.example.project.model.HardcodedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UploadUiState(
    val selectedFileName: String? = null,
    val selectedFormat: String = "PDF",
    val isConverting: Boolean = false
)

class UploadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    val supportedFormats = HardcodedData.supportedFormats

    fun onFileSelected(name: String) {
        _uiState.value = _uiState.value.copy(selectedFileName = name)
    }

    fun onFormatSelected(format: String) {
        _uiState.value = _uiState.value.copy(selectedFormat = format)
    }

    fun onConvertClick() {
        // TODO: implement conversion logic
    }
}
