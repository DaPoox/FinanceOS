package com.daprox.financeos.presentation.budget

import com.daprox.financeos.presentation.budget.component.enveloperow.EnvelopeRowUiState

/** A resolved envelope group ready for the Budget screen — label already mapped, type not exposed to the UI. */
data class BudgetEnvelopeGroup(
    val label: String,
    val envelopes: List<EnvelopeRowUiState>,
)
