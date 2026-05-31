package com.daprox.financeos.presentation.fixes

/**
 * Actions dispatched from the Fixes screen to the ViewModel.
 *
 * Subtypes:
 * - [OnBackClick]: Navigate back to the previous screen
 * - [OnRetry]: Retry after an error
 * - [OnAddEnvelopeClick]: FAB tapped — opens the new fixed charge sheet
 * - [OnNewEnvelopeDismiss]: New envelope sheet dismissed
 * - [OnNewEnvelopeSaved]: Save new fixed charge envelope from the sheet
 */
sealed interface FixesUiAction {
    data object OnBackClick : FixesUiAction
    data object OnRetry : FixesUiAction
    data object OnAddEnvelopeClick : FixesUiAction
    data object OnNewEnvelopeDismiss : FixesUiAction
    data class OnNewEnvelopeSaved(
        val name: String,
        val typeKey: String,
        val iconKey: String,
        val amount: Double,
    ) : FixesUiAction
}
