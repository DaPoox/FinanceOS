package com.daprox.financeos.presentation.accountform

/**
 * One-shot navigation events from [AccountFormViewModel] to [AccountFormScreenRoot].
 *
 * Emitted via [Channel] to trigger navigation without recreating state on configuration changes.
 * Delete dialog visibility is managed via [AccountFormUiState.showDeleteDialog].
 */
sealed interface AccountFormUiEvent {
  /** Navigation triggered after successful save or delete. */
  data object NavigateBack : AccountFormUiEvent
}
