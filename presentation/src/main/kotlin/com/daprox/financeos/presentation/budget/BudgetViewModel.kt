package com.daprox.financeos.presentation.budget

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.domain.model.EnvelopeTypeEnum as DomainEnvelopeType
import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.usecase.AddTransactionUseCase
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.ObserveCurrentMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthAllocationsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthTransactionsUseCase
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCardUiState
import com.daprox.financeos.presentation.budget.component.enveloperow.EnvelopeRowUiState
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val TYPE_LABELS = mapOf(
    EnvelopeTypeEnum.FIXED to "Fixes",
    EnvelopeTypeEnum.VARIABLE to "Variables",
    EnvelopeTypeEnum.MONTHLY to "Du mois",
    EnvelopeTypeEnum.PERMANENT to "Permanentes",
    EnvelopeTypeEnum.SAVINGS to "Épargne",
    EnvelopeTypeEnum.INVESTMENT to "Investissement",
)

private fun DomainEnvelopeType.toPresentation(): EnvelopeTypeEnum = EnvelopeTypeEnum.valueOf(name)

private fun List<EnvelopeRowUiState>.toGroups(): List<BudgetEnvelopeGroup> =
    TYPE_LABELS.entries.mapNotNull { (type, label) ->
        val items = filter { it.type == type }
        if (items.isEmpty()) null else BudgetEnvelopeGroup(label, items)
    }

class BudgetViewModel(
    private val observeCurrentMonth: ObserveCurrentMonthUseCase,
    private val observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    private val observeMonthAllocations: ObserveMonthAllocationsUseCase,
    private val observeMonthTransactions: ObserveMonthTransactionsUseCase,
    private val addTransaction: AddTransactionUseCase,
) : ViewModel() {

    private var currentMonthId = ""
    private val _retryTrigger = MutableStateFlow(0)

    private val _state = MutableStateFlow(BudgetUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<BudgetUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        _retryTrigger
            .flatMapLatest {
                observeCurrentMonth()
                    .filterNotNull()
                    .flatMapLatest { month ->
                        currentMonthId = month.id
                        combine(
                            observeActiveEnvelopes(),
                            observeMonthAllocations(month.id),
                            observeMonthTransactions(month.id),
                        ) { envelopes, allocations, transactions ->
                            val allocMap = allocations.associateBy { it.envelopeId }
                            val spendMap = transactions.groupBy { it.envelopeId }
                                .mapValues { (_, txs) -> txs.sumOf { it.amount } }

                            val rows = envelopes.map { env ->
                                val alloc = allocMap[env.id]
                                val allocated = alloc?.allocated ?: 0.0
                                val accumulated = alloc?.accumulated ?: 0.0
                                val spent = spendMap[env.id] ?: 0.0
                                val type = env.type.toPresentation()
                                val status = when {
                                    type == EnvelopeTypeEnum.FIXED -> EnvelopeStatusEnum.FIXED
                                    spent > allocated -> EnvelopeStatusEnum.OVER
                                    allocated > 0 && spent / allocated > 0.8 -> EnvelopeStatusEnum.WARNING
                                    else -> EnvelopeStatusEnum.OK
                                }
                                val progress = if (allocated > 0) (spent / allocated).toFloat().coerceIn(0f, 1f) else 0f
                                EnvelopeRowUiState(
                                    id = env.id,
                                    name = env.name,
                                    icon = iconKeyToImageVector(env.iconKey),
                                    type = type,
                                    spent = spent,
                                    allocated = allocated,
                                    accumulated = accumulated,
                                    status = status,
                                    progress = progress,
                                )
                            }

                            BudgetUiState(
                                isLoading = false,
                                monthLabel = month.label,
                                globalCard = BudgetGlobalCardUiState(
                                    income = month.income,
                                    totalSpent = rows.sumOf { it.spent },
                                    totalAllocated = rows.sumOf { it.allocated },
                                ),
                                groups = rows.toGroups(),
                                expenseEnvelopes = rows.map { EnvelopeChipUiState(it.id, it.name, it.icon) },
                            )
                        }
                    }
                    .catch { e ->
                        Log.e("BudgetViewModel", "Flow error", e)
                        emit(BudgetUiState(isLoading = false, isError = true))
                    }
            }
            .onEach { newState ->
                // Preserve transient UI state (expense sheet, saving indicator) on data refresh
                _state.update { current ->
                    newState.copy(
                        isExpenseSheetVisible = current.isExpenseSheetVisible,
                        isSaving = current.isSaving,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: BudgetUiAction) {
        viewModelScope.launch {
            when (action) {
                is BudgetUiAction.OnAllouerClick -> _events.send(BudgetUiEvent.NavigateToAllocation)
                is BudgetUiAction.OnEnvelopeClick -> _events.send(BudgetUiEvent.NavigateToEnvelopeDetail(action.id))
                is BudgetUiAction.OnAddExpenseClick -> _state.update { it.copy(isExpenseSheetVisible = true) }
                is BudgetUiAction.OnExpenseDismiss -> _state.update { it.copy(isExpenseSheetVisible = false) }
                is BudgetUiAction.OnExpenseSave -> {
                    _state.update { it.copy(isSaving = true) }
                    when (addTransaction(action.envelopeId, currentMonthId, action.amount, action.note)) {
                        is Result.Success -> _state.update { it.copy(isSaving = false, isExpenseSheetVisible = false) }
                        is Result.Error -> _state.update { it.copy(isSaving = false) }
                    }
                }
                is BudgetUiAction.OnRetry -> {
                    _state.update { it.copy(isLoading = true, isError = false) }
                    _retryTrigger.update { it + 1 }
                }
            }
        }
    }
}
