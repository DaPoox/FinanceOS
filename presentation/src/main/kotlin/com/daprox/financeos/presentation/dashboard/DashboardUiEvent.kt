package com.daprox.financeos.presentation.dashboard

sealed interface DashboardUiEvent {
    data object NavigateToBudget : DashboardUiEvent
    data class NavigateToEnvelopeDetail(val id: String) : DashboardUiEvent
    data class NavigateToMonthHistory(val id: String) : DashboardUiEvent
    data object NavigateToAllocation : DashboardUiEvent
}
