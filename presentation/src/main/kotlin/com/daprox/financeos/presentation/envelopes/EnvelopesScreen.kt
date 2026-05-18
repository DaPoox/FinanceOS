package com.daprox.financeos.presentation.envelopes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun EnvelopesScreenRoot(
    onNavigateToEdit: () -> Unit = {},
    viewModel: EnvelopesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    EnvelopesScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToEdit = onNavigateToEdit,
    )
}

@Composable
fun EnvelopesScreen(
    state: EnvelopesUiState,
    onAction: (EnvelopesUiAction) -> Unit,
    onNavigateToEdit: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize())
}
