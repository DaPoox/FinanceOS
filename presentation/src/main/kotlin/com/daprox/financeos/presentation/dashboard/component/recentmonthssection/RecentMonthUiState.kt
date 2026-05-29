package com.daprox.financeos.presentation.dashboard.component.recentmonthssection

/**
 * UI model for a recent month card in the RecentMonthsSection.
 *
 * @property id month identifier
 * @property monthLabel month name (e.g., "Avril 2026")
 * @property revenueLabel formatted revenue label
 * @property contribAmountLabel contribution amount (e.g., "+1 840 €" or "—")
 * @property contribLabel contribution description text
 * @property status month performance status (POSITIVE, WARNING, NEGATIVE)
 */
data class RecentMonthUiState(
    val id: String,
    val monthLabel: String,
    val revenueLabel: String,
    val contribAmountLabel: String,
    val contribLabel: String,
    val status: MonthStatusEnum,
)
