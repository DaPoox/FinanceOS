package com.daprox.financeos.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daprox.financeos.presentation.core.designsystem.component.FinanceOSBottomNav
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onAction: (DashboardUiAction) -> Unit,
) {
    Scaffold(
        bottomBar = {
            FinanceOSBottomNav(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(DashboardUiAction.OnTabSelected(it)) },
            )
        }
    ) { innerPadding ->
        // Content built section by section — empty for now.
        Box(Modifier.fillMaxSize().padding(innerPadding))
    }
}
