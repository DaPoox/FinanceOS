package com.daprox.financeos.presentation.envelopedetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.domain.model.EnvelopeTypeEnum as DomainEnvelopeType
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.ObserveCurrentMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveEnvelopeTransactionsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthAllocationsUseCase
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset

private val FR_MONTHS = arrayOf("jan", "fév", "mar", "avr", "mai", "jun", "jul", "aoû", "sep", "oct", "nov", "déc")
private fun Long.toDateLabel(): String {
    val ld = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
    return "${ld.dayOfMonth} ${FR_MONTHS[ld.monthValue - 1]}"
}

private fun DomainEnvelopeType.toPresentation(): EnvelopeTypeEnum = EnvelopeTypeEnum.valueOf(name)

private val TYPE_LABELS = mapOf(
    EnvelopeTypeEnum.FIXED to "Enveloppe fixe",
    EnvelopeTypeEnum.VARIABLE to "Variable standard",
    EnvelopeTypeEnum.MONTHLY to "Enveloppe du mois",
    EnvelopeTypeEnum.PERMANENT to "Permanente · accumulée",
    EnvelopeTypeEnum.SAVINGS to "Épargne",
    EnvelopeTypeEnum.INVESTMENT to "Investissement",
)

class EnvelopeDetailViewModel(
    private val id: String,
    private val observeCurrentMonth: ObserveCurrentMonthUseCase,
    private val observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    private val observeMonthAllocations: ObserveMonthAllocationsUseCase,
    private val observeEnvelopeTransactions: ObserveEnvelopeTransactionsUseCase,
) : ViewModel() {

    private val _retryTrigger = MutableStateFlow(0)

    private val _state = MutableStateFlow(EnvelopeDetailUiState(id = id))
    val state = _state.asStateFlow()

    private val _events = Channel<EnvelopeDetailUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        _retryTrigger
            .flatMapLatest {
                observeCurrentMonth()
                    .filterNotNull()
                    .flatMapLatest { month ->
                        combine(
                            observeActiveEnvelopes().map { list -> list.find { it.id == id } },
                            observeMonthAllocations(month.id),
                            observeEnvelopeTransactions(id, month.id),
                        ) { envelope, allocations, transactions ->
                            val env = envelope ?: return@combine _state.value.copy(isLoading = false)
                            val allocation = allocations.find { it.envelopeId == id }
                            val allocated = allocation?.allocated ?: 0.0
                            val accumulated = allocation?.accumulated ?: 0.0
                            val spent = transactions.sumOf { it.amount }
                            val type = env.type.toPresentation()
                            val status = when {
                                type == EnvelopeTypeEnum.FIXED -> EnvelopeStatusEnum.FIXED
                                spent > allocated -> EnvelopeStatusEnum.OVER
                                allocated > 0 && spent / allocated > 0.8 -> EnvelopeStatusEnum.WARNING
                                else -> EnvelopeStatusEnum.OK
                            }
                            EnvelopeDetailUiState(
                                isLoading = false,
                                id = id,
                                name = env.name,
                                typeLabel = TYPE_LABELS[type] ?: "",
                                type = type,
                                spent = spent,
                                allocated = allocated,
                                accumulated = accumulated,
                                status = status,
                                transactions = transactions
                                    .sortedByDescending { it.date }
                                    .map { tx -> TransactionUiState(tx.id, tx.note, tx.date.toDateLabel(), tx.amount) },
                            )
                        }
                    }
                    .catch { e ->
                        Log.e("EnvelopeDetailViewModel", "Flow error", e)
                        emit(EnvelopeDetailUiState(id = id, isLoading = false, isError = true))
                    }
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun onAction(action: EnvelopeDetailUiAction) {
        viewModelScope.launch {
            when (action) {
                is EnvelopeDetailUiAction.OnBackClick -> _events.send(EnvelopeDetailUiEvent.NavigateBack)
                is EnvelopeDetailUiAction.OnModifierAllocationClick -> Unit
                is EnvelopeDetailUiAction.OnRetry -> {
                    _state.update { it.copy(isLoading = true, isError = false) }
                    _retryTrigger.update { it + 1 }
                }
            }
        }
    }
}
