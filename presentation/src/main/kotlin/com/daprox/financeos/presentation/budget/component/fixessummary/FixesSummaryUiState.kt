package com.daprox.financeos.presentation.budget.component.fixessummary

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data for the collapsible fixed-charges card shown in BudgetScreen.
 *
 * @property totalAllocated total allocated for all fixed envelopes
 * @property totalSpent total spent for all fixed envelopes
 * @property charges list of individual fixed envelope charges
 */
data class FixesSummaryUiState(
    val totalAllocated: Double = 0.0,
    val totalSpent: Double = 0.0,
    val charges: List<FixesSummaryChargeUiState> = emptyList(),
)

/**
 * Single fixed charge item in the FixesSummaryCard.
 *
 * @property id envelope identifier
 * @property name charge name (e.g., "Loyer")
 * @property icon charge icon
 * @property spent amount spent
 * @property allocated budget allocated
 */
data class FixesSummaryChargeUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val spent: Double,
    val allocated: Double,
)
