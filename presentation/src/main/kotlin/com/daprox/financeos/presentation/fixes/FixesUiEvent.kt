package com.daprox.financeos.presentation.fixes

sealed interface FixesUiEvent {
    data object NavigateBack : FixesUiEvent
}
