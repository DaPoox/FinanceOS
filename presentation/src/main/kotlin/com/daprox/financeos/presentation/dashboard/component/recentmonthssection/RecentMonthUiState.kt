package com.daprox.financeos.presentation.dashboard.component.recentmonthssection

data class RecentMonthUiState(
    val id: String,
    val monthLabel: String,
    val revenueLabel: String,
    val contribAmountLabel: String,
    val contribLabel: String,
    val status: MonthStatusEnum,
)
