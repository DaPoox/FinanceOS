package com.daprox.financeos.presentation.history

/**
 * User actions dispatched from the history screen to [HistoryViewModel].
 *
 * Minimal action set; screen is primarily read-only with only error retry.
 */
sealed interface HistoryUiAction {
  /** User tapped retry on error state. */
  data object OnRetry : HistoryUiAction
}
