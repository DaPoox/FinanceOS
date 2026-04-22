package com.daprox.financeos.presentation.envelopes.model

// Budget allocation categories. Used to look up the correct semantic color
// via MaterialTheme.categoryColors in composables — no raw Color references
// leak into the ViewModel or UI model.
enum class EnvelopeCategory {
    FIXED_EXPENSES,
    INVESTMENT,
    SAVINGS,
    OTHER,
}
