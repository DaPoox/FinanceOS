package com.daprox.financeos.presentation.allocation

/**
 * Represents a grouping of envelopes by type (e.g., Variables, Du mois, Épargne).
 *
 * Used in the Allocation Step 3 to display collapsible sections.
 *
 * @param label The group display label (e.g., "Variables", "Fixes", "Épargne")
 * @param envelopes The list of [AllocationEnvelopeUiState]s in this group
 */
data class AllocationEnvelopeGroup(val label: String, val envelopes: List<AllocationEnvelopeUiState>)
