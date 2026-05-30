package com.daprox.financeos.presentation.accountform

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.model.AccountTypeEnum
import com.daprox.financeos.domain.usecase.DeleteAccountUseCase
import com.daprox.financeos.domain.usecase.GetAccountByIdUseCase
import com.daprox.financeos.domain.usecase.SaveAccountUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for account creation and editing screens.
 *
 * Manages form state (name, type, balance, cap, color) and provides two modes:
 * - **Create**: null [accountId]
 * - **Edit**: non-null [accountId], loads account and populates form
 *
 * Validates before save (name required). Cap field is only persisted for EPARGNE type.
 * Deletion (edit mode) is handled via [DeleteAccountUseCase].
 *
 * @param accountId null = create mode; non-null = edit mode for that account
 * @param getAccountById fetches account when loading in edit mode
 * @param saveAccount persists new or updated account
 * @param deleteAccount deletes account in edit mode
 */
class AccountFormViewModel(
    private val accountId: String?,
    private val getAccountById: GetAccountByIdUseCase,
    private val saveAccount: SaveAccountUseCase,
    private val deleteAccount: DeleteAccountUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountFormUiState(accountId = accountId))
    val state = _state.asStateFlow()

    private val _events = Channel<AccountFormUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (accountId != null) loadAccount(accountId)
    }

    /** Fetches account data and populates form fields in edit mode. */
    private fun loadAccount(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val account = getAccountById(id)
            if (account != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        name = account.name,
                        type = account.type,
                        balance = account.balance.toLong().toString(),
                        cap = account.cap?.toLong()?.toString() ?: "",
                        colorHex = account.colorHex,
                    )
                }
            } else {
                Log.e("AccountFormViewModel", "Account $id not found")
                _state.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    /** Handles all user actions from the form screen. */
    fun onAction(action: AccountFormUiAction) {
        viewModelScope.launch {
            when (action) {
                is AccountFormUiAction.OnNameChanged ->
                    _state.update { it.copy(name = action.value) }

                is AccountFormUiAction.OnTypeSelected ->
                    _state.update { it.copy(type = action.type) }

                is AccountFormUiAction.OnBalanceChanged ->
                    _state.update { it.copy(balance = action.value) }

                is AccountFormUiAction.OnCapChanged ->
                    _state.update { it.copy(cap = action.value) }

                is AccountFormUiAction.OnColorSelected ->
                    _state.update { it.copy(colorHex = action.hex) }

                is AccountFormUiAction.OnSaveClick -> save()

                is AccountFormUiAction.OnDeleteClick ->
                    _state.update { it.copy(showDeleteDialog = true) }

                is AccountFormUiAction.OnDeleteConfirmed -> deleteConfirmed()

                is AccountFormUiAction.OnDeleteDismissed ->
                    _state.update { it.copy(showDeleteDialog = false) }

                is AccountFormUiAction.OnBackClick ->
                    _events.send(AccountFormUiEvent.NavigateBack)
            }
        }
    }

    /** Validates form and persists account. Cap is only saved for EPARGNE type. Clears isSaving flag on error. */
    private fun save() {
        viewModelScope.launch {
            val s = _state.value
            if (s.name.isBlank()) return@launch
            _state.update { it.copy(isSaving = true) }
            val account = Account(
                id = accountId ?: "",
                name = s.name.trim(),
                type = s.type,
                balance = s.balance.toDoubleOrNull() ?: 0.0,
                cap = if (s.type == AccountTypeEnum.EPARGNE) s.cap.toDoubleOrNull() else null,
                colorHex = s.colorHex,
            )
            when (saveAccount(account)) {
                is Result.Success -> _events.send(AccountFormUiEvent.NavigateBack)
                is Result.Error -> _state.update { it.copy(isSaving = false) }
            }
        }
    }

    /** Deletes the account in edit mode. */
    private fun deleteConfirmed() {
        val id = accountId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            when (deleteAccount(id)) {
                is Result.Success -> _events.send(AccountFormUiEvent.NavigateBack)
                is Result.Error -> _state.update { it.copy(isSaving = false) }
            }
        }
    }
}
