package com.daprox.financeos.presentation.expense

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * UI model for a selectable envelope chip in the [ExpenseSheet].
 *
 * Represents a budget envelope option that users can select when recording a new expense.
 *
 * @property id unique envelope identifier
 * @property name display name of the envelope
 * @property icon icon vector for visual representation
 */
data class EnvelopeChipUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
)
