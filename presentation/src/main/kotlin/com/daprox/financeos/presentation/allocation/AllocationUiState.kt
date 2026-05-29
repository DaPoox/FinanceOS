package com.daprox.financeos.presentation.allocation

import androidx.compose.runtime.Stable

@Stable
data class AllocationUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val step: Int = 0,
    val monthLabel: String = "Mai 2026",
    val income: String = "4200",
    val selectedTemplate: TemplateTypeEnum = TemplateTypeEnum.PREVIOUS,
    val groups: List<AllocationEnvelopeGroup> = emptyList(),
    val remaining: Double = 0.0,
    val isSaving: Boolean = false,
    // Swipe-to-delete undo state — non-null while the undo snackbar is showing
    val lastRemovedEnvelope: AllocationEnvelopeUiState? = null,
    // New envelope bottom sheet
    val isNewEnvelopeSheetVisible: Boolean = false,
    val newEnvelopePresetType: String? = null,
)
