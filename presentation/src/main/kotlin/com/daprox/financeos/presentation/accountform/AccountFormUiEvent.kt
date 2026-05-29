package com.daprox.financeos.presentation.accountform

/**
 * One-shot navigation and dialog events from [AccountFormViewModel] to [AccountFormScreenRoot].
 *
 * Emitted via [Channel] to trigger navigation and dialogs without recreating state on configuration changes.
 */
sealed interface AccountFormUiEvent {
  /** Navigation triggered after successful save or delete. */
  data object NavigateBack : AccountFormUiEvent

  /** Request to display the delete confirmation dialog. */
  data object ShowDeleteDialog : AccountFormUiEvent
}
