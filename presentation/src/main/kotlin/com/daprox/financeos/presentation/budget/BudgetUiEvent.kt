package com.daprox.financeos.presentation.budget

sealed interface BudgetUiEvent {
    data object NavigateToAllocation : BudgetUiEvent
    data class NavigateToEnvelopeDetail(val id: String) : BudgetUiEvent
}
