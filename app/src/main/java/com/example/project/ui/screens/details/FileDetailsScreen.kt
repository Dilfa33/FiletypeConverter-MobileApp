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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project.model.ConversionStatus
import com.example.project.model.HardcodedData
import com.example.project.ui.screens.details.components.DetailRow
import com.example.project.ui.theme.ErrorRed
import com.example.project.ui.theme.SuccessGreen
import com.example.project.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailsScreen(
    fileId: String,
    fileName: String,
    onBack: () -> Unit
) {
    // Look up real data by fileId, fall back to fileName arg if not found
    val file = HardcodedData.files.find { it.id == fileId }

    val displayName   = file?.name           ?: fileName
    val origFormat    = file?.originalFormat ?: "—"
    val targetFormat  = file?.targetFormat   ?: "—"
    val sizeMb        = file?.sizeMb?.let { "%.2f MB".format(it) } ?: "—"
    val date          = file?.date           ?: "—"
    val statusLabel   = when (file?.status) {
        ConversionStatus.SUCCESS    -> "Success"
        ConversionStatus.FAILED     -> "Failed"
        ConversionStatus.PROCESSING -> "Processing"
        null                        -> "—"
    }
    val statusColor = when (file?.status) {
        ConversionStatus.SUCCESS    -> SuccessGreen
        ConversionStatus.FAILED     -> ErrorRed
        else                        -> TextSecondary
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // File preview placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow(label = "File Name",         value = displayName)
                DetailRow(label = "Original Format",   value = origFormat)
                DetailRow(label = "Converted Format",  value = targetFormat)
                DetailRow(label = "File Size",         value = sizeMb)
                DetailRow(label = "Date Converted",    value = date)
                DetailRow(
                    label = "Status",
                    value = statusLabel,
                    valueColor = statusColor
                )
            }

            OutlinedButton(
                onClick = { /* TODO: share/download file */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Download File")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
