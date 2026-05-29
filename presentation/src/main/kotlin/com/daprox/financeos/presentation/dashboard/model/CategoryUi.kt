package com.daprox.financeos.presentation.dashboard.model

import androidx.compose.ui.graphics.Color

/**
 * UI model for a single budget category shown in the donut chart.
 * Colors are resolved from the theme at the call site and baked in here.
 *
 * @property label category name
 * @property formattedAmount formatted amount (e.g., "420 €")
 * @property formattedPercentage formatted percentage (e.g., "42 %")
 * @property fraction pie slice fraction (0f..1f). Caller ensures all fractions sum to ≤1f
 * @property color pie slice color from theme
 */
data class CategoryUi(
    val label: String,
    val formattedAmount: String,
    val formattedPercentage: String,
    val fraction: Float,
    val color: Color,
)
