package com.daprox.financeos.presentation.dashboard.model

// UI model for a single liquidity card (e.g. cash available, credit line).
data class LiquidityItemUi(
    val label: String,
    val formattedAmount: String,
    val subtitle: String,
)
