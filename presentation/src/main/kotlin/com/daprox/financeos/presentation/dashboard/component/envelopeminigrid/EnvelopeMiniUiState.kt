package com.daprox.financeos.presentation.dashboard.component.envelopeminigrid

import androidx.compose.ui.graphics.vector.ImageVector

data class EnvelopeMiniUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val type: EnvelopeType,
    val spent: Double,
    val allocated: Double,
    val status: EnvelopeStatus,
    val progress: Float,
)
