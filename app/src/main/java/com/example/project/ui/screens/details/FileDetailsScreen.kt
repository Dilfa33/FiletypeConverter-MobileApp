package com.example.project.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import com.example.project.ui.screens.details.components.DetailRow
import com.example.project.ui.theme.ErrorRed
import com.example.project.ui.theme.LightBlue
import com.example.project.ui.theme.SuccessGreen
import com.example.project.ui.theme.TextSecondary
import com.example.project.viewmodel.FileDetailsUiState
import com.example.project.viewmodel.FileDetailsViewModel
import kotlinx.coroutines.launch

// Stateful
@Composable
fun FileDetailsScreen(fileName: String, onBack: () -> Unit) {
    val viewModel: FileDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    FileDetailsContent(
        uiState           = uiState,
        fallbackName      = fileName,
        snackbarHostState = snackbarHostState,
        onBack            = onBack
    )
}

// Stateless
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileDetailsContent(
    uiState: FileDetailsUiState,
    fallbackName: String,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit
) {
    val title = if (uiState is FileDetailsUiState.Success) uiState.file.name else fallbackName

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(title, style = MaterialTheme.typography.titleLarge,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is FileDetailsUiState.Init, is FileDetailsUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LightBlue)
                }
            }
            is FileDetailsUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is FileDetailsUiState.Success -> {
                FileDetailsBody(
                    file              = uiState.file,
                    snackbarHostState = snackbarHostState,
                    modifier          = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun FileDetailsBody(
    file: FileItem,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val statusLabel = when (file.status) {
        ConversionStatus.SUCCESS    -> "Success"
        ConversionStatus.FAILED     -> "Failed"
        ConversionStatus.PROCESSING -> "Processing"
    }
    val statusColor = when (file.status) {
        ConversionStatus.SUCCESS -> SuccessGreen
        ConversionStatus.FAILED  -> ErrorRed
        else                     -> TextSecondary
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Description, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(52.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(file.name, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            DetailRow(label = "File Name",        value = file.name)
            DetailRow(label = "Original Format",  value = file.originalFormat)
            DetailRow(label = "Converted Format", value = file.targetFormat)
            DetailRow(label = "File Size",        value = "%.2f MB".format(file.sizeMb))
            DetailRow(label = "Date Converted",   value = file.date)
            DetailRow(label = "Status",           value = statusLabel, valueColor = statusColor)
        }

        OutlinedButton(
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("${file.name} saved to Downloads")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Download File") }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
