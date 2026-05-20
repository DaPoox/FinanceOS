package com.daprox.financeos.presentation.dashboard

sealed interface DashboardUiAction {
    data object OnBudgetMonthClick : DashboardUiAction
    data object OnAllocateBudgetClick : DashboardUiAction
    data object OnSeeAllEnvelopesClick : DashboardUiAction
    data class OnEnvelopeClick(val id: String) : DashboardUiAction
    data class OnRecentMonthClick(val id: String) : DashboardUiAction
}
