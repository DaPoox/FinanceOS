package com.daprox.financeos.presentation.dashboard.component.envelopeminigrid

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * UI model for a mini envelope card in the grid on Home screen.
 *
 * @property id envelope identifier
 * @property name envelope display name
 * @property icon envelope icon
 * @property type envelope budget type (FIXED, VARIABLE, etc.)
 * @property spent amount spent in current month
 * @property allocated budget allocated for current month
 * @property status spending status (OK, WARNING, OVER, FIXED)
 * @property progress normalized spending ratio (0.0-1.0)
 */
data class EnvelopeMiniUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val type: EnvelopeTypeEnum,
    val spent: Double,
    val allocated: Double,
    val status: EnvelopeStatusEnum,
    val progress: Float,
)
