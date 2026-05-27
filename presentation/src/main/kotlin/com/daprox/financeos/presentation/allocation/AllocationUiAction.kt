package com.daprox.financeos.presentation.allocation

sealed interface AllocationUiAction {
    data object OnNext : AllocationUiAction
    data object OnBack : AllocationUiAction
    data class OnIncomeChanged(val value: String) : AllocationUiAction
    data class OnTemplateSelected(val template: TemplateTypeEnum) : AllocationUiAction
    data class OnEnvelopeAmountChanged(val id: String, val amount: String) : AllocationUiAction
    data object OnRetry : AllocationUiAction
}
