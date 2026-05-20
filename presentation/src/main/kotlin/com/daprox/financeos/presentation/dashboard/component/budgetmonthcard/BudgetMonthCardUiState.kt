package com.daprox.financeos.presentation.dashboard.component.budgetmonthcard

/** Ui model for the budget month card on the Home screen. */
data class BudgetMonthCardUiState(
    val monthLabel: String = "",
    val income: Double = 0.0,
    val monthSpent: Double = 0.0,
    val monthAllocated: Double = 0.0,
    val isAllocated: Boolean = false,
)
