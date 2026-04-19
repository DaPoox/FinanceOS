package com.daprox.financeos.presentation.dashboard.model

import androidx.compose.ui.graphics.Color

// UI model for a single budget category shown in the donut chart.
// Colors are resolved from the theme at the call site and baked in here —
// the chart composable itself is theme-agnostic.
data class CategoryUi(
    val label: String,
    val formattedAmount: String,
    val formattedPercentage: String,  // e.g. "42 %"
    val fraction: Float,              // 0f..1f — caller ensures all fractions sum to ≤ 1f
    val color: Color,
)
