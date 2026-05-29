package com.daprox.financeos.presentation.allocation

sealed interface AllocationUiAction {
    data object OnNext : AllocationUiAction
    data object OnBack : AllocationUiAction
    data class OnIncomeChanged(val value: String) : AllocationUiAction
    data class OnTemplateSelected(val template: TemplateTypeEnum) : AllocationUiAction
    data class OnEnvelopeAmountChanged(val id: String, val amount: String) : AllocationUiAction
    data object OnRetry : AllocationUiAction
    // Swipe-to-delete
    data class OnEnvelopeDeleted(val envelope: AllocationEnvelopeUiState) : AllocationUiAction
    data object OnEnvelopeRestored : AllocationUiAction
    data object OnClearRemovedEnvelope : AllocationUiAction
    // New envelope sheet
    data class OnAddEnvelopeClick(val typeKey: String) : AllocationUiAction
    data object OnNewEnvelopeDismiss : AllocationUiAction
    data class OnNewEnvelopeSaved(
        val name: String,
        val typeKey: String,
        val iconKey: String,
        val amount: Double,
    ) : AllocationUiAction
}
