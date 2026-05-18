package com.daprox.financeos.presentation.envelopes.model

// UI model for a single allocation envelope row.
// Children, when non-empty, are revealed when the row is expanded.
// Colors are resolved from the theme in the composable via [EnvelopeCategory] —
// no raw Color values here so the ViewModel stays Compose-free.
data class EnvelopeUi(
    val id: String,
    val name: String,
    val subtitle: String, // e.g. "Rent, utilities, insurance"
    val formattedAmount: String, // e.g. "2 940,00 €"
    val formattedPercentage: String, // e.g. "35 %"
    val fraction: Float, // 0f..1f — share of total budget
    val category: EnvelopeCategory,
    val isExpanded: Boolean = false,
    val children: List<EnvelopeUi> = emptyList(),
)
