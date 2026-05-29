package com.daprox.financeos.presentation.envelopeform

import com.daprox.financeos.domain.model.EnvelopeTypeEnum

/**
 * User actions dispatched from the envelope form screen to [EnvelopeFormViewModel].
 *
 * Covers form field changes, icon selection, and save/delete/back navigation.
 */
sealed interface EnvelopeFormUiAction {
  /** User changed the envelope name field. */
  data class OnNameChanged(val value: String) : EnvelopeFormUiAction

  /** User selected a different envelope type (FIXED, VARIABLE, MONTHLY, PERMANENT, SAVINGS, INVESTMENT). */
  data class OnTypeSelected(val type: EnvelopeTypeEnum) : EnvelopeFormUiAction

  /** User selected an icon for the envelope. */
  data class OnIconSelected(val key: String) : EnvelopeFormUiAction

  /** User changed the monthly allocation amount. */
  data class OnAmountChanged(val value: String) : EnvelopeFormUiAction

  /** User changed the accumulated balance (PERMANENT envelopes). */
  data class OnAccumulatedChanged(val value: String) : EnvelopeFormUiAction

  /** User changed the savings cap (SAVINGS envelopes). */
  data class OnCapChanged(val value: String) : EnvelopeFormUiAction

  /** User tapped save button — validates and persists the envelope. */
  data object OnSaveClick : EnvelopeFormUiAction

  /** User tapped delete button — triggers deletion (edit mode only). */
  data object OnDeleteClick : EnvelopeFormUiAction

  /** User tapped back/close button — navigates back without saving. */
  data object OnBackClick : EnvelopeFormUiAction
}
