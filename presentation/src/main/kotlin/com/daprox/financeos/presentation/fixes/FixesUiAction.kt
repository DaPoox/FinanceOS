package com.daprox.financeos.presentation.fixes

/**
 * Actions dispatched from the Fixes screen to the ViewModel.
 *
 * Subtypes:
 * - [OnBackClick]: Navigate back to the previous screen
 * - [OnRetry]: Retry after an error
 */
sealed interface FixesUiAction {
    /**
     * Navigate back to the previous screen.
     */
    data object OnBackClick : FixesUiAction

    /**
     * Retry data loading after an error.
     */
    data object OnRetry : FixesUiAction
}
