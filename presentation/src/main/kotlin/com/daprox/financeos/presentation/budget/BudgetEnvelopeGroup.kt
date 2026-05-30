package com.daprox.financeos.presentation.budget

import com.daprox.financeos.presentation.budget.component.enveloperow.EnvelopeRowUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

/**
 * A resolved envelope group ready for the Budget screen.
 * Label is already mapped (e.g., "Variables", "Fixes"), type exposed for AddEnvelopeRow routing.
 *
 * @property label group display label (e.g., "Variables", "Épargne")
 * @property envelopes list of envelopes in this group
 * @property type envelope type for this group, used to preset type when adding a new envelope
 */
data class BudgetEnvelopeGroup(
    val label: String,
    val envelopes: List<EnvelopeRowUiState>,
    val type: EnvelopeTypeEnum,
)
