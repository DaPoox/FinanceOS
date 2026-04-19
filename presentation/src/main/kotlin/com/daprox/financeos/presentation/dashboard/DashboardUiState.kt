package com.daprox.financeos.presentation.dashboard

import com.daprox.financeos.presentation.core.designsystem.component.DashboardTab
import com.daprox.financeos.presentation.dashboard.model.ProgressBarUi

// UI state for the dashboard screen.
data class DashboardUiState(
    val selectedTab: DashboardTab = DashboardTab.WEALTH,
    val progressBars: List<ProgressBarUi> = emptyList(),
)
