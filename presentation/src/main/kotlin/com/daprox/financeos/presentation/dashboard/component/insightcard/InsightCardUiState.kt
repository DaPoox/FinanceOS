package com.daprox.financeos.presentation.dashboard.component.insightcard

/** Ui model for the contextual insight card on the Home screen. */
data class InsightCardUiState(
    val type: InsightTypeEnum,
    val title: String,
    val subtitle: String,
    val highlightAmount: Double? = null,
)
