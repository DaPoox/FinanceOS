package com.daprox.financeos.presentation.history

import androidx.compose.runtime.Stable

@Stable
data class HistoryUiState(
    val totalIncome: Double = 0.0,
    val totalContrib: Double = 0.0,
    val avgSavingRate: Int = 0,
    val barData: List<Double> = emptyList(),
    val months: List<MonthRowUiState> = emptyList(),
)
