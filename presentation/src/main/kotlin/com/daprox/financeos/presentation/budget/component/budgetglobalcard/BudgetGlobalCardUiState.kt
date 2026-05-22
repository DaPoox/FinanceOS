package com.daprox.financeos.presentation.budget.component.budgetglobalcard

/** Ui model for the global budget summary card on the Budget screen. */
data class BudgetGlobalCardUiState(
    val income: Double = 0.0,
    val totalSpent: Double = 0.0,
    val totalAllocated: Double = 0.0,
)
