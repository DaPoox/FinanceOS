package com.daprox.financeos.presentation.envelopes.edit

sealed interface EnvelopesEditUiAction {
    // Slider dragged to a new position.
    data class OnSliderChanged(val id: String, val fraction: Float) : EnvelopesEditUiAction
    // + button: nudge allocation up by 1%.
    data class OnIncrement(val id: String) : EnvelopesEditUiAction
    // − button: nudge allocation down by 1%.
    data class OnDecrement(val id: String) : EnvelopesEditUiAction
    // check_circle tapped — persists changes (no-op until domain layer is wired).
    data object OnConfirm : EnvelopesEditUiAction
}
