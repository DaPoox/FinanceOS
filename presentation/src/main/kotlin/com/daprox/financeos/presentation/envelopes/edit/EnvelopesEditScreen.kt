package com.daprox.financeos.presentation.envelopes.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun EnvelopesEditScreenRoot(
    viewModel: EnvelopesEditViewModel = koinViewModel(),
    onClose: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    EnvelopesEditScreen(
        state = state,
        onAction = viewModel::onAction,
        onClose = onClose,
        onConfirm = {
            viewModel.onAction(EnvelopesEditUiAction.OnConfirm)
            onConfirm()
        },
    )
}

@Composable
fun EnvelopesEditScreen(
    state: EnvelopesEditUiState,
    onAction: (EnvelopesEditUiAction) -> Unit,
    onClose: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize())
}
