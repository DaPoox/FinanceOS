package com.daprox.financeos.presentation.envelopedetail

/**
 * Actions dispatched from the Envelope Detail screen to the ViewModel.
 *
 * Subtypes:
 * - [OnBackClick]: Navigate back to the previous screen
 * - [OnModifierAllocationClick]: Reserved for future allocation editing
 * - [OnRetry]: Retry after an error
 * - [OnMenuClick]: Open the 3-dot overflow menu
 * - [OnMenuDismiss]: Close the overflow menu
 * - [OnRenameClick]: Navigate to the envelope edit screen
 * - [OnArchiveClick]: Open the archive confirmation dialog
 * - [OnArchiveConfirm]: Confirm archiving the envelope
 * - [OnArchiveDismiss]: Close the archive dialog without archiving
 */
sealed interface EnvelopeDetailUiAction {
    /**
     * Navigate back to the previous screen.
     */
    data object OnBackClick : EnvelopeDetailUiAction

    /**
     * Reserved for future allocation editing functionality.
     */
    data object OnModifierAllocationClick : EnvelopeDetailUiAction

    /**
     * Retry data loading after an error.
     */
    data object OnRetry : EnvelopeDetailUiAction

    /**
     * Open the 3-dot overflow menu.
     */
    data object OnMenuClick : EnvelopeDetailUiAction

    /**
     * Close the overflow menu.
     */
    data object OnMenuDismiss : EnvelopeDetailUiAction

    /**
     * Navigate to the envelope edit screen (via menu).
     */
    data object OnRenameClick : EnvelopeDetailUiAction

    /**
     * Open the archive confirmation dialog (via menu).
     */
    data object OnArchiveClick : EnvelopeDetailUiAction

    /**
     * Confirm archiving the envelope.
     */
    data object OnArchiveConfirm : EnvelopeDetailUiAction

    /**
     * Close the archive dialog without archiving.
     */
    data object OnArchiveDismiss : EnvelopeDetailUiAction
}
