package com.daprox.financeos.presentation.dashboard

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.core.designsystem.component.DashboardTab
import com.daprox.financeos.presentation.dashboard.model.CategoryUi
import com.daprox.financeos.presentation.dashboard.model.LiquidityItemUi
import com.daprox.financeos.presentation.dashboard.model.NetWorthUi
import com.daprox.financeos.presentation.dashboard.model.ProgressBarUi
import com.daprox.financeos.presentation.dashboard.model.TransactionUi

// UI state for the dashboard screen.
@Stable
data class DashboardUiState(
    val selectedTab: DashboardTab = DashboardTab.WEALTH,
    val progressBars: List<ProgressBarUi> = emptyList(),
    val netWorth: NetWorthUi = NetWorthUi(label = "NET WORTH", formattedAmount = "0 €"),
    val categories: List<CategoryUi> = emptyList(),
    val totalCategoryAmount: String = "0 €",
    val selectedCategoryIndex: Int? = null,
    val showCategoryAmounts: Boolean = true,
    val liquidityItems: List<LiquidityItemUi> = emptyList(),
    val recentTransactions: List<TransactionUi> = emptyList(),
)
