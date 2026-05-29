package com.daprox.financeos.presentation.budget.component.fixessummary

import androidx.compose.ui.graphics.vector.ImageVector

/** Data for the collapsible fixed-charges card shown in BudgetScreen. */
data class FixesSummaryUiState(
    val totalAllocated: Double = 0.0,
    val totalSpent: Double = 0.0,
    val charges: List<FixesSummaryChargeUiState> = emptyList(),
)

data class FixesSummaryChargeUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val spent: Double,
    val allocated: Double,
)
