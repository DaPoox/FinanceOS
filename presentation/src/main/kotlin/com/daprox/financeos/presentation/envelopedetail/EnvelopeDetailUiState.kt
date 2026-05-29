package com.daprox.financeos.presentation.envelopedetail

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

@Stable
data class EnvelopeDetailUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isSaving: Boolean = false,
    val id: String = "",
    val name: String = "",
    val typeLabel: String = "",
    val type: EnvelopeTypeEnum = EnvelopeTypeEnum.VARIABLE,
    val spent: Double = 0.0,
    val allocated: Double = 0.0,
    val accumulated: Double = 0.0,
    val status: EnvelopeStatusEnum = EnvelopeStatusEnum.OK,
    val transactions: List<TransactionUiState> = emptyList(),
    val monthlyHistory: List<Double> = emptyList(),
    val monthsAgo: Int = 0,
    // ⋯ menu state
    val isMenuVisible: Boolean = false,
    val isArchiveDialogVisible: Boolean = false,
)
