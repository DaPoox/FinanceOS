package com.daprox.financeos.presentation.envelopes

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.envelopes.model.EnvelopeUi

// UI state for the Envelopes screen.
// [expandedId] tracks which envelope row is currently open (null = all collapsed).
@Stable
data class EnvelopesUiState(
    val totalFormattedAmount: String = "0,00 €",
    val envelopes: List<EnvelopeUi> = emptyList(),
    val expandedId: String? = null,
)
