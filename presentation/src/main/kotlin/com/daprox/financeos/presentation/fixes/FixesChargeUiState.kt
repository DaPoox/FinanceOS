package com.daprox.financeos.presentation.fixes

import androidx.compose.ui.graphics.vector.ImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum

/**
 * UI model for a fixed charge row in the Fixes screen.
 *
 * Represents a single FIXED envelope with its spending status for the current month.
 *
 * @param id The envelope's unique identifier
 * @param name The envelope's display name
 * @param icon The envelope's icon as an [ImageVector]
 * @param spent The amount spent this month
 * @param allocated The amount allocated this month
 * @param status The [EnvelopeStatusEnum] status badge (FIXED, OK, WARNING, OVER)
 */
data class FixesChargeUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val spent: Double,
    val allocated: Double,
    val status: EnvelopeStatusEnum,
)
