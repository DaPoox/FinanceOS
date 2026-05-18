package com.daprox.financeos.presentation.envelopes.model

// Budget allocation categories. Composables resolve the display color via
// MaterialTheme.finColors — no raw Color references leak into the ViewModel.
enum class EnvelopeCategory {
    FIXED_EXPENSES,
    INVESTMENT,
    SAVINGS,
    OTHER,
}
