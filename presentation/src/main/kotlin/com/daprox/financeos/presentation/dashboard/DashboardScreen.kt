package com.daprox.financeos.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigateToEnvelopes: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DashboardScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToEnvelopes = onNavigateToEnvelopes,
    )
}

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onAction: (DashboardUiAction) -> Unit,
    onNavigateToEnvelopes: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize())
}
