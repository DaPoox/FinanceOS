package com.daprox.financeos.presentation.accountform

import com.daprox.financeos.domain.model.AccountTypeEnum

sealed interface AccountFormUiAction {
    data class OnNameChanged(val value: String) : AccountFormUiAction
    data class OnTypeSelected(val type: AccountTypeEnum) : AccountFormUiAction
    data class OnBalanceChanged(val value: String) : AccountFormUiAction
    data class OnCapChanged(val value: String) : AccountFormUiAction
    data class OnColorSelected(val hex: String) : AccountFormUiAction
    data object OnSaveClick : AccountFormUiAction
    data object OnDeleteClick : AccountFormUiAction
    data object OnDeleteConfirmed : AccountFormUiAction
    data object OnDeleteDismissed : AccountFormUiAction
    data object OnBackClick : AccountFormUiAction
}
