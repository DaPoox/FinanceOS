package com.daprox.financeos.presentation.allocation

sealed interface AllocationUiEvent {
    data object NavigateBack : AllocationUiEvent
}
