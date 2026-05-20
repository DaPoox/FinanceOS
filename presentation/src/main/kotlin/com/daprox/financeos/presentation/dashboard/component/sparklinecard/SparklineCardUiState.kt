package com.daprox.financeos.presentation.dashboard.component.sparklinecard

data class SparklineCardUiState(
    val data: List<Double>,
    val monthLabels: List<String>,
    val pctLabel: String,
    val trend: SparklineTrendEnum,
)
