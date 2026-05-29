package com.daprox.financeos.presentation.dashboard.model

/**
 * UI model for a single progress bar row in budget breakdown.
 *
 * @property label progress bar label (envelope name)
 * @property formattedAmount formatted spending amount
 * @property progress spending ratio (0f..1f, clamped by caller)
 * @property isGradient true=week gradient (blue), false=month solid (green)
 */
data class ProgressBarUi(
    val label: String,
    val formattedAmount: String,
    val progress: Float,
    val isGradient: Boolean,
)
