package com.daprox.financeos.presentation.patrimoine

/**
 * One-shot navigation events from [PatrimoineViewModel] to [PatrimoineScreenRoot].
 *
 * Emitted via [Channel] to trigger navigation without recreating state on configuration changes.
 */
sealed interface PatrimoineUiEvent {
  /** Navigate to AccountFormScreen in create mode (empty state CTA or "+" button). */
  data object NavigateToAddAccount : PatrimoineUiEvent

  /** Navigate to AccountFormScreen in edit mode for the given account. */
  data class NavigateToEditAccount(val id: String) : PatrimoineUiEvent
}
