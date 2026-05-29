package com.daprox.financeos.presentation.allocation

/**
 * One-shot events emitted by the Allocation ViewModel to trigger navigation or user feedback.
 *
 * Events are consumed via a Channel and should be observed with [ObserveAsEvents].
 * Unlike state, events are not replayed on recomposition.
 *
 * Subtypes:
 * - [NavigateBack]: Navigate back to the previous screen (after successful allocation)
 * - [ShowError]: Show an error message to the user (e.g., failed to save allocation)
 */
sealed interface AllocationUiEvent {
    /**
     * Navigate back to the previous screen (after successful allocation save).
     */
    data object NavigateBack : AllocationUiEvent

    /**
     * Show an error message to the user.
     */
    data object ShowError : AllocationUiEvent
}
