package com.daprox.financeos.presentation.patrimoine

import androidx.compose.runtime.Stable

@Stable
data class PatrimoineUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val netWorth: Double = 0.0,
    val deltaLabel: String = "",
    val deltaPct: String = "",
    val savings: Double = 0.0,
    val investment: Double = 0.0,
    val liquid: Double = 0.0,
    val sparklineData: List<Double> = emptyList(),
    val sparklineMonths: List<String> = emptyList(),
    val selectedRange: SparklineRangeEnum = SparklineRangeEnum.M12,
    val accounts: List<AccountUiState> = emptyList(),
)
