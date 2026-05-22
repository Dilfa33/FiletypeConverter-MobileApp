package com.example.project.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project.ui.screens.history.components.EmptyHistoryState
import com.example.project.ui.screens.history.components.HistoryListItem
import com.example.project.ui.screens.history.components.HistorySearchBar
import com.example.project.ui.screens.history.components.StatusFilterRow
import com.example.project.ui.theme.LightBlue
import com.example.project.viewmodel.HistoryUiState
import com.example.project.viewmodel.HistoryViewModel
import com.example.project.viewmodel.StatusFilter
import kotlinx.coroutines.launch

// Stateful — owns the ViewModel
@Composable
fun HistoryScreen(onFileClick: (fileId: String, fileName: String) -> Unit) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    HistoryContent(
        uiState = uiState,
        onFileClick = onFileClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onFilterSelected = viewModel::onFilterSelected,
        onClearSearch = viewModel::clearSearch
    )
}

// Stateless — pure rendering
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryContent(
    uiState: HistoryUiState,
    onFileClick: (fileId: String, fileName: String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (StatusFilter) -> Unit,
    onClearSearch: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (showScrollToTop) {
                FloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    containerColor = LightBlue
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
                }
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is HistoryUiState.Init, is HistoryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = LightBlue) }
            }
            is HistoryUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error) }
            }
            is HistoryUiState.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HistorySearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = onSearchQueryChange,
                        onClear = onClearSearch
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusFilterRow(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelected = onFilterSelected
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (uiState.isEmpty) {
                        EmptyHistoryState()
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.filteredFiles, key = { it.id }) { file ->
                                HistoryListItem(
                                    file = file,
                                    onClick = { onFileClick(file.id, file.name) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
