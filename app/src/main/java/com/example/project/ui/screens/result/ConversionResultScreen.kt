package com.example.project.ui.screens.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project.model.FileItem
import com.example.project.ui.screens.result.components.ResultCard
import com.example.project.ui.theme.LightBlue
import com.example.project.viewmodel.ConversionResultUiState
import com.example.project.viewmodel.ConversionResultViewModel

// Stateful — owns the ViewModel
@Composable
fun ConversionResultScreen(
    fileName: String,
    onViewDetails: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ConversionResultViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    ConversionResultContent(
        uiState = uiState,
        fallbackName = fileName,
        onViewDetails = onViewDetails,
        onBack = onBack
    )
}

// Stateless — pure rendering
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversionResultContent(
    uiState: ConversionResultUiState,
    fallbackName: String,
    onViewDetails: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Result", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        when (uiState) {
            is ConversionResultUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LightBlue)
                }
            }
            is ConversionResultUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is ConversionResultUiState.Success -> {
                ConversionResultBody(
                    file = uiState.file,
                    onViewDetails = onViewDetails,
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
private fun ConversionResultBody(
    file: FileItem,
    onViewDetails: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        ResultCard(
            fileName = file.name,
            originalFormat = file.originalFormat,
            targetFormat = file.targetFormat,
            sizeMb = file.sizeMb
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onViewDetails, modifier = Modifier.weight(1f)) {
                Text("View Details")
            }
            Button(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Done")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
