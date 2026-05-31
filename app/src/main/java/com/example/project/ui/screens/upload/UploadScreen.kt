package com.example.project.ui.screens.upload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project.ui.screens.upload.components.FormatSelector
import com.example.project.ui.screens.upload.components.UploadCard
import com.example.project.ui.theme.LightBlue
import com.example.project.viewmodel.UploadUiState
import com.example.project.viewmodel.UploadViewModel

// Stateful — owns the ViewModel
@Composable
fun UploadScreen(onConversionComplete: (fileId: String, fileName: String) -> Unit) {
    val viewModel: UploadViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UploadUiState.Success) {
            val success = uiState as UploadUiState.Success
            if (success.savedFileId != null) {
                onConversionComplete(success.savedFileId, success.selectedFileName ?: "file")
                viewModel.onConversionNavigated()
            }
        }
    }

    UploadContent(
        uiState = uiState,
        onFileSelected = { uri, name -> viewModel.onFileSelected(uri, name) },
        onFormatSelected = viewModel::onFormatSelected,
        onConvertClick = viewModel::onConvertClick
    )
}

// Stateless — pure rendering
@Composable
private fun UploadContent(
    uiState: UploadUiState,
    onFileSelected: (android.net.Uri, String) -> Unit,
    onFormatSelected: (String) -> Unit,
    onConvertClick: () -> Unit
) {
    when (uiState) {
        is UploadUiState.Init, is UploadUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LightBlue)
            }
        }
        is UploadUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
            }
        }
        is UploadUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "FileCast",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Convert files between formats instantly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                UploadCard(
                    selectedFileName = uiState.selectedFileName,
                    onFileSelected = onFileSelected
                )

                FormatSelector(
                    selectedFormat = uiState.selectedFormat,
                    formats = uiState.formats,
                    onFormatSelected = onFormatSelected
                )

                Spacer(modifier = Modifier.weight(1f))

                if (uiState.selectedFileName == null) {
                    Text(
                        text = "Select a file to enable conversion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = onConvertClick,
                    enabled = uiState.selectedFileName != null && !uiState.isConverting,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (uiState.isConverting) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.height(20.dp)
                        )
                    } else {
                        Text("Convert File")
                    }
                }
            }
        }
    }
}
