package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.di.SessionManager
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import com.example.project.model.HardcodedData
import com.example.project.repository.FileRepository
import com.example.project.repository.FormatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

sealed interface UploadUiState {
    object Init : UploadUiState
    object Loading : UploadUiState
    data class Success(
        val formats: List<String>,
        val selectedFileName: String? = null,
        val selectedFormat: String = "PDF",
        val isConverting: Boolean = false,
        val savedFileId: String? = null
    ) : UploadUiState
    data class Error(val message: String) : UploadUiState
}

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val formatRepository: FormatRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Init)
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    init {
        loadFormats()
    }

    private fun loadFormats() {
        viewModelScope.launch {
            _uiState.value = UploadUiState.Loading
            try {
                formatRepository.getAllFormats().collect { entities ->
                    val formats = entities.map { it.name }.ifEmpty { HardcodedData.supportedFormats }
                    val current = _uiState.value as? UploadUiState.Success
                    _uiState.value = UploadUiState.Success(
                        formats = formats,
                        selectedFileName = current?.selectedFileName,
                        selectedFormat = current?.selectedFormat ?: formats.first()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UploadUiState.Error(e.message ?: "Failed to load formats")
            }
        }
    }

    fun onFileSelected(name: String) {
        val current = _uiState.value as? UploadUiState.Success ?: return
        _uiState.value = current.copy(selectedFileName = name, savedFileId = null)
    }

    fun onFormatSelected(format: String) {
        val current = _uiState.value as? UploadUiState.Success ?: return
        _uiState.value = current.copy(selectedFormat = format)
    }

    fun onConvertClick() {
        val current = _uiState.value as? UploadUiState.Success ?: return
        val fileName = current.selectedFileName ?: return
        viewModelScope.launch {
            _uiState.value = current.copy(isConverting = true, savedFileId = null)
            try {
                val fileId = UUID.randomUUID().toString()
                val originalFormat = fileName.substringAfterLast(".", "TXT").uppercase()
                val file = FileItem(
                    id = fileId,
                    name = fileName,
                    originalFormat = originalFormat,
                    targetFormat = current.selectedFormat,
                    sizeMb = (1..100).random().toFloat(),
                    date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
                    status = ConversionStatus.SUCCESS
                )
                fileRepository.insertFile(file, sessionManager.currentUserId)
                _uiState.value = current.copy(isConverting = false, savedFileId = fileId)
            } catch (e: Exception) {
                _uiState.value = UploadUiState.Error(e.message ?: "Conversion failed")
            }
        }
    }

    fun onConversionNavigated() {
        val current = _uiState.value as? UploadUiState.Success ?: return
        _uiState.value = current.copy(savedFileId = null)
    }
}
