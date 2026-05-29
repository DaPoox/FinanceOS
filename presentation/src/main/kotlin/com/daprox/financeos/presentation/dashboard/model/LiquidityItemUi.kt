package com.daprox.financeos.presentation.dashboard.model

/**
 * UI model for a single liquidity card (e.g., cash available, credit line).
 *
 * @property label liquidity type label
 * @property formattedAmount formatted amount
 * @property subtitle descriptive subtitle
 */
data class LiquidityItemUi(
    val label: String,
    val formattedAmount: String,
    val subtitle: String,
)
