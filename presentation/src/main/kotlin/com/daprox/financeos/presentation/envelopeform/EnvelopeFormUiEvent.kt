package com.daprox.financeos.presentation.envelopeform

/**
 * One-shot navigation events from [EnvelopeFormViewModel] to [EnvelopeFormScreenRoot].
 *
 * Emitted via [Channel] to trigger navigation without recreating state on configuration changes.
 */
sealed interface EnvelopeFormUiEvent {
  /** Navigation triggered after successful save or delete. */
  data object NavigateBack : EnvelopeFormUiEvent
}
