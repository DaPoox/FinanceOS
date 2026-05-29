package com.daprox.financeos.presentation.envelopedetail

/**
 * One-shot events emitted by the Envelope Detail ViewModel to trigger navigation or user feedback.
 *
 * Events are consumed via a Channel and should be observed with [ObserveAsEvents].
 * Unlike state, events are not replayed on recomposition.
 *
 * Subtypes:
 * - [NavigateBack]: Navigate back to the previous screen
 * - [NavigateToEditEnvelope]: Navigate to the envelope edit screen for a given envelope ID
 */
sealed interface EnvelopeDetailUiEvent {
    /**
     * Navigate back to the previous screen.
     */
    data object NavigateBack : EnvelopeDetailUiEvent

    /**
     * Navigate to the envelope edit screen.
     *
     * @param id The envelope ID to edit
     */
    data class NavigateToEditEnvelope(val id: String) : EnvelopeDetailUiEvent
}
