package com.daprox.financeos.presentation.history

data class MonthRowUiState(
    val monthLabel: String,
    val income: Double,
    val spent: Double,
    val contrib: Double,
    val status: MonthStatusEnum,
)
