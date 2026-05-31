package com.example.project.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.local.util.ConversionEngine
import com.example.project.data.remote.dto.CreateConversionDto
import com.example.project.di.SessionManager
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import com.example.project.model.HardcodedData
import com.example.project.repository.FileRepository
import com.example.project.repository.FirestoreConversionRepository
import com.example.project.repository.FormatRepository
import com.example.project.repository.NetworkConversionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
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
    private val sessionManager: SessionManager,
    private val networkRepository: NetworkConversionRepository,
    private val firestoreRepository: FirestoreConversionRepository,
    private val conversionEngine: ConversionEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Init)
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    // Hold the URI in-memory (not part of UiState — not serializable)
    private var selectedUri: Uri? = null

    init { loadFormats() }

    private fun loadFormats() {
        viewModelScope.launch {
            _uiState.value = UploadUiState.Loading
            try {
                formatRepository.getAllFormats().collect { entities ->
                    val formats = entities.map { it.name }.ifEmpty { HardcodedData.supportedFormats }
                    val current = _uiState.value as? UploadUiState.Success
                    _uiState.value = UploadUiState.Success(
                        formats        = formats,
                        selectedFileName = current?.selectedFileName,
                        selectedFormat = current?.selectedFormat ?: formats.first()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UploadUiState.Error(e.message ?: "Failed to load formats")
            }
        }
    }

    fun onFileSelected(uri: Uri, name: String) {
        selectedUri = uri
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
        val uri = selectedUri ?: return

        viewModelScope.launch {
            _uiState.value = current.copy(isConverting = true, savedFileId = null)
            try {
                // ── Try CloudConvert via backend API (max 6 s) ───────────────
                val result: ConversionEngine.ConversionResult = try {
                    withTimeout(6_000L) {
                        val bytes    = conversionEngine.readBytes(uri)
                        val response = networkRepository.convertFile(bytes, fileName, current.selectedFormat)
                        conversionEngine.downloadFromUrl(
                            response.downloadUrl,
                            response.fileName,
                            current.selectedFormat
                        )
                    }
                } catch (_: Exception) {
                    // Backend offline / timeout / no key → fast local fallback
                    conversionEngine.convert(uri, fileName, current.selectedFormat)
                }

                val fileId = UUID.randomUUID().toString()
                val originalFormat = fileName.substringAfterLast(".", "BIN").uppercase()
                val today = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

                val file = FileItem(
                    id             = fileId,
                    name           = fileName,
                    originalFormat = originalFormat,
                    targetFormat   = current.selectedFormat,
                    sizeMb         = result.sizeMb,
                    date           = today,
                    status         = ConversionStatus.SUCCESS,
                    outputPath     = result.outputUri
                )
                fileRepository.insertFile(file, sessionManager.currentUserId)

                // Save to Firestore — best-effort
                try {
                    firestoreRepository.addConversion(file, sessionManager.currentUserId)
                } catch (_: Exception) {}

                // POST to remote API — best-effort
                try {
                    networkRepository.createConversion(
                        CreateConversionDto(
                            name           = fileName,
                            originalFormat = originalFormat,
                            targetFormat   = current.selectedFormat,
                            sizeMb         = result.sizeMb,
                            date           = today,
                            status         = "SUCCESS"
                        )
                    )
                } catch (_: Exception) {}

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
