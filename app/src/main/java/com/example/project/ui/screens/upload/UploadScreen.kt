package com.example.project.ui.screens.upload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.ui.screens.upload.components.FormatSelector
import com.example.project.ui.screens.upload.components.UploadCard
import com.example.project.viewmodel.UploadViewModel

@Composable
fun UploadScreen(
    onConversionComplete: (String) -> Unit,
    viewModel: UploadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "FileConvert",
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
            onFileSelected = viewModel::onFileSelected
        )

        FormatSelector(
            selectedFormat = uiState.selectedFormat,
            formats = viewModel.supportedFormats,
            onFormatSelected = viewModel::onFormatSelected
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
            onClick = {
                viewModel.onConvertClick()
                onConversionComplete("stub-id")
            },
            enabled = uiState.selectedFileName != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Convert File")
        }
    }
}
