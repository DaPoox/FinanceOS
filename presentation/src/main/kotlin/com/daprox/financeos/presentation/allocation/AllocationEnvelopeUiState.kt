package com.daprox.financeos.presentation.allocation

import androidx.compose.ui.graphics.vector.ImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

/**
 * UI model for an envelope in the Allocation screen's Step 3.
 *
 * Represents a single envelope with its allocated amount for the current month.
 * Used in both the adjustment list and the undo snackbar state.
 *
 * @param id The envelope's unique identifier
 * @param name The envelope's display name
 * @param icon The envelope's icon as an [ImageVector]
 * @param type The envelope's [EnvelopeTypeEnum] (FIXED, VARIABLE, MONTHLY, PERMANENT, SAVINGS, INVESTMENT)
 * @param amount The allocated amount for this month (numeric string)
 */
data class AllocationEnvelopeUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val type: EnvelopeTypeEnum,
    val amount: String,
)
