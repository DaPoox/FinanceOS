package com.daprox.financeos.presentation.history

import androidx.compose.runtime.Stable

/**
 * UI state for the history (financial summary) screen.
 *
 * Displays a 12-month overview with aggregate stats (income, contributions, savings rate),
 * bar chart of monthly contributions, and detailed month rows with status badges.
 *
 * @property isLoading loading state while fetching monthly data
 * @property isError error state if data fetch fails
 * @property isEmpty empty state when no months exist
 * @property totalIncome sum of all income across displayed months
 * @property totalContrib sum of all contributions (savings) across months
 * @property avgSavingRate average savings rate percentage (totalContrib / totalIncome * 100)
 * @property barData bar chart values — monthly contributions in reverse chronological order
 * @property months list of month rows with income, spent, contribution, and status badge
 */
@Stable
data class HistoryUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val totalIncome: Double = 0.0,
    val totalContrib: Double = 0.0,
    val avgSavingRate: Int = 0,
    val barData: List<Double> = emptyList(),
    val months: List<MonthRowUiState> = emptyList(),
)
