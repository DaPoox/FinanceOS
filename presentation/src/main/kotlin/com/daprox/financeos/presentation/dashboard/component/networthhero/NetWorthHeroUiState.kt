package com.daprox.financeos.presentation.dashboard.component.networthhero

/** Ui model for the NetWorth hero card on the Home screen. */
data class NetWorthHeroUiState(
    val netWorth: Double = 0.0,
    val delta: Double = 0.0,
    val insightLabel: String? = null,
    val contribSavings: Double = 0.0,
    val contribInvest: Double = 0.0,
)
