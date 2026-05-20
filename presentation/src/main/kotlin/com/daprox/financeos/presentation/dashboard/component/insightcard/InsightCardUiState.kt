package com.daprox.financeos.presentation.dashboard.component.insightcard

enum class InsightType { WARNING, ERROR, SUCCESS }

/** Ui model for the contextual insight card on the Home screen. */
data class InsightCardUiState(
    val type: InsightType,
    val title: String,
    val subtitle: String,
    val highlightAmount: Double? = null,
)
