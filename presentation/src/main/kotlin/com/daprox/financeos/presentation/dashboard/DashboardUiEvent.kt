package com.daprox.financeos.presentation.dashboard

/**
 * Navigation events from Dashboard screen.
 */
sealed interface DashboardUiEvent {
    /** Navigate to budget/envelope list screen */
    data object NavigateToBudget : DashboardUiEvent

    /** Navigate to envelope detail screen with envelope id */
    data class NavigateToEnvelopeDetail(val id: String) : DashboardUiEvent

    /** Navigate to month history/detail screen with month id */
    data class NavigateToMonthHistory(val id: String) : DashboardUiEvent

    /** Navigate to allocation/distribution screen */
    data object NavigateToAllocation : DashboardUiEvent
}
