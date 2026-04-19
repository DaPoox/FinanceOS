package com.daprox.financeos.presentation.dashboard

import com.daprox.financeos.presentation.core.designsystem.component.DashboardTab
import com.daprox.financeos.presentation.dashboard.model.CategoryUi
import com.daprox.financeos.presentation.dashboard.model.NetWorthUi
import com.daprox.financeos.presentation.dashboard.model.ProgressBarUi

// UI state for the dashboard screen.
@androidx.compose.runtime.Stable
data class DashboardUiState(
    val selectedTab: DashboardTab = DashboardTab.WEALTH,
    val progressBars: List<ProgressBarUi> = emptyList(),
    val netWorth: NetWorthUi = NetWorthUi(label = "NET WORTH", formattedAmount = "0 €"),
    val categories: List<CategoryUi> = emptyList(),
    val selectedCategoryIndex: Int? = null,
    val showCategoryAmounts: Boolean = true,
)
