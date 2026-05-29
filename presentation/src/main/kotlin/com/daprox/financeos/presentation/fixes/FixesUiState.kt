package com.daprox.financeos.presentation.fixes

import androidx.compose.runtime.Stable

@Stable
data class FixesUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val monthLabel: String = "",
    val totalAllocated: Double = 0.0,
    val totalSpent: Double = 0.0,
    val charges: List<FixesChargeUiState> = emptyList(),
)
