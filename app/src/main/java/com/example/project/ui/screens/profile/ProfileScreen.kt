package com.example.project.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project.ui.screens.profile.components.ProfileHeader
import com.example.project.ui.screens.profile.components.RecentFileItem
import com.example.project.ui.screens.profile.components.StatCard
import com.example.project.ui.theme.LightBlue
import com.example.project.ui.theme.TextSecondary
import com.example.project.viewmodel.ProfileUiState
import com.example.project.viewmodel.ProfileViewModel

// Stateful
@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    ProfileContent(
        uiState               = uiState,
        onUpdateUsername      = viewModel::updateUsername,
        onClearHistory        = viewModel::clearHistory,
        onToggleNotifications = viewModel::toggleNotifications
    )
}

// Stateless
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onUpdateUsername: (String) -> Unit,
    onClearHistory: () -> Unit,
    onToggleNotifications: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showNotifDialog by remember { mutableStateOf(false) }
    var showStorageDialog by remember { mutableStateOf(false) }

    if (showEditDialog && uiState is ProfileUiState.Success) {
        EditProfileDialog(
            currentUsername = uiState.username,
            onConfirm = { newName ->
                onUpdateUsername(newName)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    if (showNotifDialog && uiState is ProfileUiState.Success) {
        AlertDialog(
            onDismissRequest = { showNotifDialog = false },
            title = { Text("Notifications") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable notifications")
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { onToggleNotifications() },
                        colors = SwitchDefaults.colors(checkedThumbColor = LightBlue)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotifDialog = false }) { Text("Done") }
            }
        )
    }

    if (showStorageDialog) {
        AlertDialog(
            onDismissRequest = { showStorageDialog = false },
            title = { Text("Storage & Data") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState is ProfileUiState.Success) {
                        Text("Files stored: ${uiState.totalConversions}")
                        Text("Storage used: ${"%.1f".format(uiState.storageUsedMb)} MB")
                    }
                    Text(
                        "Clear history will permanently delete all your conversion records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showStorageDialog = false
                    }
                ) { Text("Clear History", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showStorageDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is ProfileUiState.Init, is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = LightBlue) }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error) }
            }
            is ProfileUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileHeader(
                            name = uiState.username,
                            email = uiState.email,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { StatCard(label = "Conversions", value = "${uiState.totalConversions}") }
                            item { StatCard(label = "Storage (MB)", value = "%.1f".format(uiState.storageUsedMb)) }
                            item { StatCard(label = "Success %", value = "${uiState.successRate}%") }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        Text("Recent Conversions", style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    items(uiState.recentFiles, key = { it.id }) { file ->
                        RecentFileItem(file = file,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        Text("Settings", style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        Button(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) { Text("Edit Profile", color = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item {
                        Button(
                            onClick = { showNotifDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) { Text("Notifications", color = MaterialTheme.colorScheme.onSurface) }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item {
                        Button(
                            onClick = { showStorageDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) { Text("Storage & Data", color = MaterialTheme.colorScheme.onSurface) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentUsername: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newName by remember { mutableStateOf(currentUsername) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Username") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(newName) }, enabled = newName.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
