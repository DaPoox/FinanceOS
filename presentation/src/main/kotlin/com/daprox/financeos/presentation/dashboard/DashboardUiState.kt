package com.daprox.financeos.presentation.dashboard

import com.daprox.financeos.presentation.core.designsystem.component.DashboardTab

// UI state for the dashboard screen.
data class DashboardUiState(
    val selectedTab: DashboardTab = DashboardTab.WEALTH,
)
