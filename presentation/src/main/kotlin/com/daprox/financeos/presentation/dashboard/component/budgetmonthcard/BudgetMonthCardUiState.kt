package com.daprox.financeos.presentation.dashboard.component.budgetmonthcard

/**
 * UI model for the budget month card on the Home screen.
 *
 * @property monthLabel month name (e.g., "Mai")
 * @property income monthly income amount
 * @property monthSpent total spent across variable/monthly envelopes
 * @property monthAllocated total allocated across variable/monthly envelopes
 * @property isAllocated whether month has allocations (shows CTA to allocate if false)
 */
data class BudgetMonthCardUiState(
    val monthLabel: String = "",
    val income: Double = 0.0,
    val monthSpent: Double = 0.0,
    val monthAllocated: Double = 0.0,
    val isAllocated: Boolean = false,
)
