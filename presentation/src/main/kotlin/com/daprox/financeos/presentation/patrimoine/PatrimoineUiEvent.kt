package com.daprox.financeos.presentation.patrimoine

sealed interface PatrimoineUiEvent {
    // Empty state CTA or "+" header button — opens AccountFormScreen in create mode
    data object NavigateToAddAccount : PatrimoineUiEvent
    // Row tap — opens AccountFormScreen in edit mode for the given account id
    data class NavigateToEditAccount(val id: String) : PatrimoineUiEvent
}
