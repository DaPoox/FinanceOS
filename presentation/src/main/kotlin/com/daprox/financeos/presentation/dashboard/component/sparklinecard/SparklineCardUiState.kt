package com.daprox.financeos.presentation.dashboard.component.sparklinecard

/**
 * UI model for the net worth sparkline card (6-month trend).
 *
 * @property data list of net worth values for each month
 * @property monthLabels month abbreviations to display below chart
 * @property pctLabel percentage change label (e.g., "+11.8%")
 * @property trend positive or negative trend for styling
 */
data class SparklineCardUiState(
    val data: List<Double>,
    val monthLabels: List<String>,
    val pctLabel: String,
    val trend: SparklineTrendEnum,
)
