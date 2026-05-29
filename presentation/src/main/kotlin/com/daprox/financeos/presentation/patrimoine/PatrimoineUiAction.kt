package com.daprox.financeos.presentation.patrimoine

/**
 * User actions dispatched from the patrimoine screen to [PatrimoineViewModel].
 *
 * Covers chart range selection, retry, and account navigation (create/edit).
 */
sealed interface PatrimoineUiAction {
  /** User selected a different sparkline chart range (M6, M12, Y3). */
  data class OnRangeSelected(val range: SparklineRangeEnum) : PatrimoineUiAction

  /** User tapped retry on error state. */
  data object OnRetry : PatrimoineUiAction

  /** User tapped CTA in empty state to add first account. */
  data object OnAddAccountCta : PatrimoineUiAction

  /** User tapped "+" header button in accounts section to add account. */
  data object OnAddAccountClick : PatrimoineUiAction

  /** User tapped account row to edit — opens AccountFormScreen in edit mode. */
  data class OnAccountClick(val id: String) : PatrimoineUiAction
}
