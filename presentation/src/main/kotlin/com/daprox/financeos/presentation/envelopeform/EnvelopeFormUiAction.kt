package com.daprox.financeos.presentation.envelopeform

import com.daprox.financeos.domain.model.EnvelopeTypeEnum

sealed interface EnvelopeFormUiAction {
    data class OnNameChanged(val value: String) : EnvelopeFormUiAction
    data class OnTypeSelected(val type: EnvelopeTypeEnum) : EnvelopeFormUiAction
    data class OnIconSelected(val key: String) : EnvelopeFormUiAction
    data class OnAmountChanged(val value: String) : EnvelopeFormUiAction
    data class OnAccumulatedChanged(val value: String) : EnvelopeFormUiAction
    data class OnCapChanged(val value: String) : EnvelopeFormUiAction
    data object OnSaveClick : EnvelopeFormUiAction
    data object OnDeleteClick : EnvelopeFormUiAction
    data object OnBackClick : EnvelopeFormUiAction
}
