package com.daprox.financeos.presentation.expense

import androidx.compose.ui.graphics.vector.ImageVector

/** Ui model for a selectable envelope chip in the ExpenseSheet. */
data class EnvelopeChipUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
)
