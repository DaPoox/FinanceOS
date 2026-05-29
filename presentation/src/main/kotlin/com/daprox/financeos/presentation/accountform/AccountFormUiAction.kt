package com.daprox.financeos.presentation.accountform

import com.daprox.financeos.domain.model.AccountTypeEnum

/**
 * User actions dispatched from the account form screen to [AccountFormViewModel].
 *
 * Covers form field changes, color selection, and save/delete/back navigation.
 * Delete has a confirmation flow with [OnDeleteConfirmed] and [OnDeleteDismissed].
 */
sealed interface AccountFormUiAction {
  /** User changed the account name field. */
  data class OnNameChanged(val value: String) : AccountFormUiAction

  /** User selected a different account type (COURANT, EPARGNE, INVESTISSEMENT). */
  data class OnTypeSelected(val type: AccountTypeEnum) : AccountFormUiAction

  /** User changed the account balance field. */
  data class OnBalanceChanged(val value: String) : AccountFormUiAction

  /** User changed the max balance cap (EPARGNE accounts). */
  data class OnCapChanged(val value: String) : AccountFormUiAction

  /** User selected a color from the palette. */
  data class OnColorSelected(val hex: String) : AccountFormUiAction

  /** User tapped save button — validates and persists the account. */
  data object OnSaveClick : AccountFormUiAction

  /** User tapped delete button — shows confirmation dialog (edit mode only). */
  data object OnDeleteClick : AccountFormUiAction

  /** User confirmed deletion in the dialog. */
  data object OnDeleteConfirmed : AccountFormUiAction

  /** User dismissed the delete confirmation dialog. */
  data object OnDeleteDismissed : AccountFormUiAction

  /** User tapped back/close button — navigates back without saving. */
  data object OnBackClick : AccountFormUiAction
}
