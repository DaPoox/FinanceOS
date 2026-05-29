package com.daprox.financeos.presentation.budget

/**
 * Navigation events from Budget screen.
 */
sealed interface BudgetUiEvent {
    /** Navigate to allocation/distribution screen */
    data object NavigateToAllocation : BudgetUiEvent

    /** Navigate to envelope detail screen with envelope id */
    data class NavigateToEnvelopeDetail(val id: String) : BudgetUiEvent

    /** Navigate to fixed charges detail/management screen */
    data object NavigateToFixes : BudgetUiEvent
}
