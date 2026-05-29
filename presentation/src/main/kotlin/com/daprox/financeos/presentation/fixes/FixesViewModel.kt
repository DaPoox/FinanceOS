package com.daprox.financeos.presentation.fixes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.domain.model.EnvelopeTypeEnum as DomainEnvelopeType
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.ObserveCurrentMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthAllocationsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthTransactionsUseCase
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

private fun DomainEnvelopeType.toPresentation(): EnvelopeTypeEnum = EnvelopeTypeEnum.valueOf(name)

/**
 * ViewModel for the Fixes screen.
 *
 * Displays fixed envelope charges (FIXED type only) for the current month,
 * showing allocated vs. spent amounts with progress indicators and status badges.
 *
 * Features:
 * - Real-time status computation (OK, WARNING, OVER)
 * - Animated progress bar for total spend
 * - Loading, error, and retry states
 * - Empty state when no fixed charges exist
 *
 * State is provided via a [StateFlow<FixesUiState>]; events are sent through a [Channel<FixesUiEvent>].
 *
 * @param observeCurrentMonth UseCase to observe the current month
 * @param observeActiveEnvelopes UseCase to observe all active envelopes
 * @param observeMonthAllocations UseCase to observe allocations for the current month
 * @param observeMonthTransactions UseCase to observe transactions for the current month
 */
class FixesViewModel(
    private val observeCurrentMonth: ObserveCurrentMonthUseCase,
    private val observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    private val observeMonthAllocations: ObserveMonthAllocationsUseCase,
    private val observeMonthTransactions: ObserveMonthTransactionsUseCase,
) : ViewModel() {

    private val _retryTrigger = MutableStateFlow(0)

    private val _state = MutableStateFlow(FixesUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<FixesUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        _retryTrigger
            .flatMapLatest {
                observeCurrentMonth()
                    .filterNotNull()
                    .flatMapLatest { month ->
                        combine(
                            // Only FIXED envelopes
                            observeActiveEnvelopes().map { list ->
                                list.filter { it.type.toPresentation() == EnvelopeTypeEnum.FIXED }
                            },
                            observeMonthAllocations(month.id),
                            observeMonthTransactions(month.id),
                        ) { envelopes, allocations, transactions ->
                            val allocMap = allocations.associateBy { it.envelopeId }
                            val spendMap = transactions.groupBy { it.envelopeId }
                                .mapValues { (_, txs) -> txs.sumOf { it.amount } }

                            val charges = envelopes.map { env ->
                                val alloc = allocMap[env.id]
                                val allocated = alloc?.allocated ?: 0.0
                                val spent = spendMap[env.id] ?: 0.0
                                val status = when {
                                    spent > allocated -> EnvelopeStatusEnum.OVER
                                    allocated > 0 && spent / allocated > 0.8 -> EnvelopeStatusEnum.WARNING
                                    else -> EnvelopeStatusEnum.FIXED
                                }
                                FixesChargeUiState(
                                    id = env.id,
                                    name = env.name,
                                    icon = iconKeyToImageVector(env.iconKey),
                                    spent = spent,
                                    allocated = allocated,
                                    status = status,
                                )
                            }

                            FixesUiState(
                                isLoading = false,
                                monthLabel = month.label,
                                totalAllocated = charges.sumOf { it.allocated },
                                totalSpent = charges.sumOf { it.spent },
                                charges = charges,
                            )
                        }
                    }
                    .catch { e ->
                        Log.e("FixesViewModel", "Flow error", e)
                        emit(FixesUiState(isLoading = false, isError = true))
                    }
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    /**
     * Processes UI actions dispatched from the Fixes screen.
     *
     * Action handlers:
     * - [FixesUiAction.OnBackClick]: Navigate back
     * - [FixesUiAction.OnRetry]: Retry after an error
     *
     * @param action The [FixesUiAction] to process
     */
    fun onAction(action: FixesUiAction) {
        viewModelScope.launch {
            when (action) {
                is FixesUiAction.OnBackClick -> _events.send(FixesUiEvent.NavigateBack)
                is FixesUiAction.OnRetry -> {
                    _state.update { it.copy(isLoading = true, isError = false) }
                    _retryTrigger.update { it + 1 }
                }
            }
        }
    }
}
