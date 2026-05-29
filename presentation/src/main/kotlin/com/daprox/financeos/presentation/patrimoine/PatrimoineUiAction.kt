package com.daprox.financeos.presentation.patrimoine

sealed interface PatrimoineUiAction {
    data class OnRangeSelected(val range: SparklineRangeEnum) : PatrimoineUiAction
    data object OnRetry : PatrimoineUiAction
    data object OnAddAccountCta : PatrimoineUiAction
    // Opens AccountFormScreen for a new account from the section "+" header
    data object OnAddAccountClick : PatrimoineUiAction
    // Opens AccountFormScreen in edit mode for the tapped account row
    data class OnAccountClick(val id: String) : PatrimoineUiAction
}
