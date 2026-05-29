package com.daprox.financeos.presentation.history

/**
 * UI model for a single month row in the history list.
 *
 * Represents one month's financial snapshot with income, spending, savings contribution,
 * and a status badge (BEST, GOOD, MID, HARD).
 *
 * @property monthLabel display text for the month (e.g. "Mai 2026")
 * @property income total income for the month
 * @property spent total expenses for the month
 * @property contrib amount contributed to savings this month
 * @property status visual status badge (reflects savings performance)
 */
data class MonthRowUiState(
    val monthLabel: String,
    val income: Double,
    val spent: Double,
    val contrib: Double,
    val status: MonthStatusEnum,
)
