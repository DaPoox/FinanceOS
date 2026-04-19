package com.daprox.financeos.presentation.dashboard.model

// UI model for a single progress bar row.
// progress is clamped to 0f..1f by the caller.
// isGradient=true  → week bar (secondary → secondaryContainer gradient, blue)
// isGradient=false → month bar (primary solid, green)
data class ProgressBarUi(
    val label: String,
    val formattedAmount: String,
    val progress: Float,
    val isGradient: Boolean,
)
