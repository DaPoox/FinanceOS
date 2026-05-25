package com.daprox.financeos.presentation.patrimoine

sealed interface PatrimoineUiEvent {
    data object NavigateToAddAccount : PatrimoineUiEvent
}
