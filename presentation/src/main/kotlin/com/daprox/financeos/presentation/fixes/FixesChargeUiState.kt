package com.daprox.financeos.presentation.fixes

import androidx.compose.ui.graphics.vector.ImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum

data class FixesChargeUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val spent: Double,
    val allocated: Double,
    val status: EnvelopeStatusEnum,
)
