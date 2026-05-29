package com.daprox.financeos.presentation.allocation

/**
 * Actions dispatched from the Allocation screen to the ViewModel.
 *
 * User interactions and system events are encoded as sealed interface subtypes.
 * The ViewModel's `onAction()` method handles each action type.
 *
 * Subtypes:
 * - [OnNext]: Advance to the next step (or finalize if on step 2)
 * - [OnBack]: Return to the previous step (or navigate back if on step 0)
 * - [OnIncomeChanged]: Update the income value
 * - [OnTemplateSelected]: Select a template for allocation copying
 * - [OnEnvelopeAmountChanged]: Update an envelope's allocated amount
 * - [OnEnvelopeDeleted]: Remove an envelope from the list (swipe delete)
 * - [OnEnvelopeRestored]: Restore the last deleted envelope (undo)
 * - [OnClearRemovedEnvelope]: Clear the undo state after snackbar dismissal
 * - [OnAddEnvelopeClick]: Open the new envelope creation sheet for a given type
 * - [OnNewEnvelopeDismiss]: Close the new envelope sheet
 * - [OnNewEnvelopeSaved]: Create a new envelope with the provided details
 * - [OnRetry]: Retry after an error
 */
sealed interface AllocationUiAction {
    /**
     * Advance to the next step or save the allocation if on the final step.
     */
    data object OnNext : AllocationUiAction

    /**
     * Return to the previous step or navigate back if on the first step.
     */
    data object OnBack : AllocationUiAction

    /**
     * Update the income value. Filters input to digits only at the screen level.
     *
     * @param value The new income value
     */
    data class OnIncomeChanged(val value: String) : AllocationUiAction

    /**
     * Select a template for allocation copying.
     *
     * @param template The selected [TemplateTypeEnum]
     */
    data class OnTemplateSelected(val template: TemplateTypeEnum) : AllocationUiAction

    /**
     * Update an envelope's allocated amount.
     *
     * @param id The envelope ID
     * @param amount The new amount as a numeric string
     */
    data class OnEnvelopeAmountChanged(val id: String, val amount: String) : AllocationUiAction

    /**
     * Retry data loading after an error.
     */
    data object OnRetry : AllocationUiAction

    /**
     * Remove an envelope from the allocation (swipe to delete).
     *
     * @param envelope The [AllocationEnvelopeUiState] to remove
     */
    data class OnEnvelopeDeleted(val envelope: AllocationEnvelopeUiState) : AllocationUiAction

    /**
     * Restore the last deleted envelope (undo via snackbar action).
     */
    data object OnEnvelopeRestored : AllocationUiAction

    /**
     * Clear the undo state after the snackbar is dismissed.
     */
    data object OnClearRemovedEnvelope : AllocationUiAction

    /**
     * Open the new envelope creation sheet for the given envelope type.
     *
     * @param typeKey The envelope type key (e.g., "VARIABLE", "MONTHLY")
     */
    data class OnAddEnvelopeClick(val typeKey: String) : AllocationUiAction

    /**
     * Close the new envelope creation sheet.
     */
    data object OnNewEnvelopeDismiss : AllocationUiAction

    /**
     * Create a new envelope with the provided details.
     *
     * @param name The envelope name
     * @param typeKey The envelope type key
     * @param iconKey The icon key (from the design system)
     * @param amount The allocated amount for this month
     */
    data class OnNewEnvelopeSaved(
        val name: String,
        val typeKey: String,
        val iconKey: String,
        val amount: Double,
    ) : AllocationUiAction
}
