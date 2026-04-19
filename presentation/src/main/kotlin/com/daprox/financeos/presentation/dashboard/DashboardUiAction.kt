package com.daprox.financeos.presentation.dashboard

import com.daprox.financeos.presentation.core.designsystem.component.DashboardTab

sealed interface DashboardUiAction {
    data class OnTabSelected(val tab: DashboardTab) : DashboardUiAction
    // Tapping a donut segment toggles selection (tap again to deselect).
    data class OnCategorySelected(val index: Int) : DashboardUiAction
    // FAB tap — navigates to the add-transaction flow (wired in a future task).
    data object OnAddTransactionClick : DashboardUiAction
}
