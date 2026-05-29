package com.daprox.financeos.presentation.budget.component.enveloperow

import androidx.compose.ui.graphics.vector.ImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

/**
 * UI model for an envelope row in the Budget screen.
 *
 * @property id envelope identifier
 * @property name envelope display name
 * @property icon envelope icon
 * @property type envelope budget type (FIXED, VARIABLE, etc.)
 * @property spent amount spent in current month
 * @property allocated budget allocated for current month
 * @property accumulated total accumulated/carried over from previous months
 * @property status spending status (OK, WARNING, OVER, FIXED)
 * @property progress normalized spending ratio (0.0-1.0)
 */
data class EnvelopeRowUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val type: EnvelopeTypeEnum,
    val spent: Double,
    val allocated: Double,
    val accumulated: Double = 0.0,
    val status: EnvelopeStatusEnum,
    val progress: Float,
)
