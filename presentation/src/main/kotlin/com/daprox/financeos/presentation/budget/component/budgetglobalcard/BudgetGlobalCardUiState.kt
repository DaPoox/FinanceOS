package com.daprox.financeos.presentation.budget.component.budgetglobalcard

/**
 * UI model for the global budget summary card on the Budget screen.
 *
 * @property income monthly income
 * @property totalSpent total amount spent across all envelopes
 * @property totalAllocated total amount allocated across all envelopes
 */
data class BudgetGlobalCardUiState(
    val income: Double = 0.0,
    val totalSpent: Double = 0.0,
    val totalAllocated: Double = 0.0,
)
