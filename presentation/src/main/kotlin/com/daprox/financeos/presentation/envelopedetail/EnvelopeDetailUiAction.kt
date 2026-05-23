package com.daprox.financeos.presentation.envelopedetail

sealed interface EnvelopeDetailUiAction {
    data object OnBackClick : EnvelopeDetailUiAction
    data object OnModifierAllocationClick : EnvelopeDetailUiAction
}
