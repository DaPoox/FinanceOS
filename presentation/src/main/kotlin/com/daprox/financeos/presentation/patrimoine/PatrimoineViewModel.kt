package com.daprox.financeos.presentation.patrimoine

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.model.AccountTypeEnum as DomainAccountType
import com.daprox.financeos.domain.usecase.ObserveAccountsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val SPARKLINE_6M = listOf(54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0)
private val SPARKLINE_6M_LABELS = listOf("déc", "jan", "fév", "mar", "avr", "mai")

private val SPARKLINE_12M = listOf(48200.0, 49100.0, 50320.0, 51100.0, 51980.0, 52900.0, 54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0)
private val SPARKLINE_12M_LABELS = listOf("jun", "jul", "aoû", "sep", "oct", "nov", "déc", "jan", "fév", "mar", "avr", "mai")

private val SPARKLINE_3Y = listOf(34000.0, 37500.0, 40200.0, 43800.0, 46100.0, 48200.0, 49100.0, 50320.0, 51100.0, 51980.0, 52900.0, 54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0)
private val SPARKLINE_3Y_LABELS = listOf("jun", "sep", "déc", "mar", "jun", "sep", "déc", "mar", "jun", "sep", "déc", "mar", "jun", "sep", "déc", "mar", "mai")

private fun sparklineFor(range: SparklineRangeEnum): Pair<List<Double>, List<String>> = when (range) {
    SparklineRangeEnum.M6 -> SPARKLINE_6M to SPARKLINE_6M_LABELS
    SparklineRangeEnum.M12 -> SPARKLINE_12M to SPARKLINE_12M_LABELS
    SparklineRangeEnum.Y3 -> SPARKLINE_3Y to SPARKLINE_3Y_LABELS
}

private fun Account.toUiState(): AccountUiState = AccountUiState(
    id = id,
    name = name,
    type = when (type) {
        DomainAccountType.COURANT -> AccountTypeEnum.COURANT
        DomainAccountType.EPARGNE -> AccountTypeEnum.EPARGNE
        DomainAccountType.INVESTISSEMENT -> AccountTypeEnum.INVESTISSEMENT
    },
    balance = balance,
    cap = cap,
    color = Color(android.graphics.Color.parseColor(colorHex)),
)

class PatrimoineViewModel(
    private val observeAccounts: ObserveAccountsUseCase,
) : ViewModel() {

    private val _retryTrigger = MutableStateFlow(0)

    private val _state = MutableStateFlow(
        run {
            val (sparkData, sparkLabels) = sparklineFor(SparklineRangeEnum.M12)
            PatrimoineUiState(sparklineData = sparkData, sparklineMonths = sparkLabels, selectedRange = SparklineRangeEnum.M12)
        }
    )
    val state = _state.asStateFlow()

    private val _events = Channel<PatrimoineUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        _retryTrigger
            .flatMapLatest {
                observeAccounts()
                    .map { accounts ->
                        val uiAccounts = accounts.map { it.toUiState() }
                        val liquid = uiAccounts.filter { it.type == AccountTypeEnum.COURANT }.sumOf { it.balance }
                        val savings = uiAccounts.filter { it.type == AccountTypeEnum.EPARGNE }.sumOf { it.balance }
                        val investment = uiAccounts.filter { it.type == AccountTypeEnum.INVESTISSEMENT }.sumOf { it.balance }
                        val currentRange = _state.value.selectedRange
                        val (sparkData, sparkLabels) = sparklineFor(currentRange)
                        PatrimoineUiState(
                            isLoading = false,
                            isEmpty = uiAccounts.isEmpty(),
                            netWorth = liquid + savings + investment,
                            savings = savings,
                            investment = investment,
                            liquid = liquid,
                            accounts = uiAccounts,
                            deltaLabel = "+12 380 € · 6 mois",
                            deltaPct = "+11.8%",
                            selectedRange = currentRange,
                            sparklineData = sparkData,
                            sparklineMonths = sparkLabels,
                        )
                    }
                    .catch { e ->
                        Log.e("PatrimoineViewModel", "Flow error", e)
                        emit(PatrimoineUiState(isLoading = false, isError = true))
                    }
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun onAction(action: PatrimoineUiAction) {
        viewModelScope.launch {
            when (action) {
                is PatrimoineUiAction.OnRangeSelected -> {
                    val (sparkData, sparkLabels) = sparklineFor(action.range)
                    _state.update { it.copy(selectedRange = action.range, sparklineData = sparkData, sparklineMonths = sparkLabels) }
                }
                is PatrimoineUiAction.OnRetry -> {
                    _state.update { it.copy(isLoading = true, isError = false) }
                    _retryTrigger.update { it + 1 }
                }
                is PatrimoineUiAction.OnAddAccountCta -> _events.send(PatrimoineUiEvent.NavigateToAddAccount)
                is PatrimoineUiAction.OnAddAccountClick -> _events.send(PatrimoineUiEvent.NavigateToAddAccount)
                is PatrimoineUiAction.OnAccountClick -> _events.send(PatrimoineUiEvent.NavigateToEditAccount(action.id))
            }
        }
    }
}
