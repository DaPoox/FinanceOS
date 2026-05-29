package com.daprox.financeos.presentation.envelopeform

sealed interface EnvelopeFormUiEvent {
    data object NavigateBack : EnvelopeFormUiEvent
}
