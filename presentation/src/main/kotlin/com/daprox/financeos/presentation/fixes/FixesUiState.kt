package com.daprox.financeos.presentation.fixes

import androidx.compose.runtime.Stable

/**
 * UI state for the Fixes screen.
 *
 * Represents the state of the fixed envelope charges view for the current month.
 *
 * @param isLoading Whether data is being loaded from the database
 * @param isError Whether a loading or processing error occurred
 * @param monthLabel Localized month label (e.g., "Mai 2026")
 * @param totalAllocated Sum of allocated amounts for all fixed charges
 * @param totalSpent Sum of spent amounts for all fixed charges
 * @param charges List of [FixesChargeUiState]s for each fixed envelope
 * @param isNewEnvelopeSheetVisible whether the new envelope creation sheet is visible
 */
@Stable
data class FixesUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val monthLabel: String = "",
    val totalAllocated: Double = 0.0,
    val totalSpent: Double = 0.0,
    val charges: List<FixesChargeUiState> = emptyList(),
    val isNewEnvelopeSheetVisible: Boolean = false,
)
