package com.daprox.financeos.presentation.envelopedetail

sealed interface EnvelopeDetailUiEvent {
    data object NavigateBack : EnvelopeDetailUiEvent
    data class NavigateToEditEnvelope(val id: String) : EnvelopeDetailUiEvent
}
