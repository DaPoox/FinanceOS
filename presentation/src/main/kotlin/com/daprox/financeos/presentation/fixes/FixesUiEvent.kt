package com.daprox.financeos.presentation.fixes

/**
 * One-shot events emitted by the Fixes ViewModel to trigger navigation or user feedback.
 *
 * Events are consumed via a Channel and should be observed with [ObserveAsEvents].
 * Unlike state, events are not replayed on recomposition.
 *
 * Subtypes:
 * - [NavigateBack]: Navigate back to the previous screen
 */
sealed interface FixesUiEvent {
    /**
     * Navigate back to the previous screen.
     */
    data object NavigateBack : FixesUiEvent
}
