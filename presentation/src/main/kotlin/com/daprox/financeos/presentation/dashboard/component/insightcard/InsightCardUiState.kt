package com.daprox.financeos.presentation.dashboard.component.insightcard

/**
 * UI model for the contextual insight card on the Home screen.
 *
 * @property type alert type (WARNING, ERROR, SUCCESS)
 * @property title main alert message
 * @property subtitle detail text with remaining budget or context
 * @property highlightAmount optional amount to highlight (unused but available for future)
 */
data class InsightCardUiState(
    val type: InsightTypeEnum,
    val title: String,
    val subtitle: String,
    val highlightAmount: Double? = null,
)
