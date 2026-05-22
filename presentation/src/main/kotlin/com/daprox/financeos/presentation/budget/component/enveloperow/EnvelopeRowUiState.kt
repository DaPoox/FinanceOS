package com.daprox.financeos.presentation.budget.component.enveloperow

import androidx.compose.ui.graphics.vector.ImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

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
