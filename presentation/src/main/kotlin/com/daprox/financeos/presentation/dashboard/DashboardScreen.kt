package com.daprox.financeos.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daprox.financeos.presentation.core.designsystem.component.DashboardTopBar
import com.daprox.financeos.presentation.core.designsystem.component.DualProgressBarsSection
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
        topBar = { DashboardTopBar() },
        bottomBar = {
            FinanceOSBottomNav(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(DashboardUiAction.OnTabSelected(it)) },
            )
        }
    ) { innerPadding ->
        // Content column — sections added one by one as the dashboard is built out.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            DualProgressBarsSection(progressBars = state.progressBars)
        }
    }
}
