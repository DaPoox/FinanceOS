package com.daprox.financeos.presentation.accountform

sealed interface AccountFormUiEvent {
    data object NavigateBack : AccountFormUiEvent
    data object ShowDeleteDialog : AccountFormUiEvent
}
