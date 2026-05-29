package com.daprox.financeos.presentation.dashboard.model

/**
 * UI model for the net worth card.
 *
 * @property label net worth label
 * @property formattedAmount pre-formatted amount
 */
data class NetWorthUi(
    val label: String,
    val formattedAmount: String,
)
