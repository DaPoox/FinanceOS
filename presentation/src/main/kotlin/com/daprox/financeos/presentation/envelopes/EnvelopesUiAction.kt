package com.daprox.financeos.presentation.envelopes

sealed interface EnvelopesUiAction {
    // Tapping a row toggles its sublist open/closed.
    data class OnEnvelopeClick(val id: String) : EnvelopesUiAction
    // "Add New Allocation" ghost button.
    data object OnAddAllocationClick : EnvelopesUiAction
}
