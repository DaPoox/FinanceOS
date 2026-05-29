package com.daprox.financeos.presentation.budget

import com.daprox.financeos.presentation.budget.component.enveloperow.EnvelopeRowUiState

/**
 * A resolved envelope group ready for the Budget screen.
 * Label is already mapped (e.g., "Variables", "Fixes"), type not exposed to UI.
 *
 * @property label group display label (e.g., "Variables", "Épargne")
 * @property envelopes list of envelopes in this group
 */
data class BudgetEnvelopeGroup(
    val label: String,
    val envelopes: List<EnvelopeRowUiState>,
)
