package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryUiState(
    val files: List<FileItem> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HistoryUiState(
            files = listOf(
                FileItem("1", "document.docx", "DOCX", "PDF", 2.4f, "Mar 28, 2026", ConversionStatus.SUCCESS),
                FileItem("2", "photo.jpg", "JPG", "PNG", 1.1f, "Mar 27, 2026", ConversionStatus.SUCCESS),
                FileItem("3", "report.pdf", "PDF", "TXT", 0.3f, "Mar 26, 2026", ConversionStatus.FAILED),
                FileItem("4", "presentation.pptx", "PPTX", "PDF", 4.7f, "Mar 25, 2026", ConversionStatus.SUCCESS),
            )
        )
    )
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
}
