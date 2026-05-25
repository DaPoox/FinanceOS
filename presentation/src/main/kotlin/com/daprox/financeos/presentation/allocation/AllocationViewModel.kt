package com.daprox.financeos.presentation.allocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.domain.model.EnvelopeTypeEnum as DomainEnvelopeType
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.ObserveCurrentMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthAllocationsUseCase
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

private fun List<AllocationEnvelopeUiState>.toGroups(): List<AllocationEnvelopeGroup> =
    TYPE_LABELS.entries.mapNotNull { (type, label) ->
        val items = filter { it.type == type }
        if (items.isEmpty()) null else AllocationEnvelopeGroup(label, items)
    }

private fun computeRemaining(income: String, groups: List<AllocationEnvelopeGroup>): Double {
    val incomeVal = income.toDoubleOrNull() ?: 0.0
    val totalAlloc = groups.flatMap { it.envelopes }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    return incomeVal - totalAlloc
}

class AllocationViewModel(
    observeCurrentMonth: ObserveCurrentMonthUseCase,
    observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    observeMonthAllocations: ObserveMonthAllocationsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AllocationUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<AllocationUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        observeCurrentMonth()
            .filterNotNull()
            .flatMapLatest { month ->
                combine(
                    observeActiveEnvelopes(),
                    observeMonthAllocations(month.id),
                ) { envelopes, allocations ->
                    val allocMap = allocations.associateBy { it.envelopeId }
                    val uiEnvelopes = envelopes.map { env ->
                        val alloc = allocMap[env.id]
                        AllocationEnvelopeUiState(
                            id = env.id,
                            name = env.name,
                            icon = iconKeyToImageVector(env.iconKey),
                            type = env.type.toPresentation(),
                            amount = alloc?.allocated?.toLong()?.toString() ?: "0",
                        )
                    }
                    val income = month.income.toLong().toString()
                    val groups = uiEnvelopes.toGroups()
                    AllocationUiState(
                        income = income,
                        groups = groups,
                        remaining = computeRemaining(income, groups),
                    )
                }
            }
            .onEach { newState ->
                // Only update non-user-edited fields on first load; preserve step and template
                _state.update { current ->
                    if (current.groups.isEmpty()) newState
                    else current.copy(income = newState.income, groups = newState.groups, remaining = newState.remaining)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: AllocationUiAction) {
        viewModelScope.launch {
            when (action) {
                is AllocationUiAction.OnNext -> {
                    val current = _state.value.step
                    if (current < 2) {
                        _state.update { it.copy(step = current + 1) }
                    } else {
                        _events.send(AllocationUiEvent.NavigateBack)
                    }
                }
                is AllocationUiAction.OnBack -> {
                    val current = _state.value.step
                    if (current > 0) {
                        _state.update { it.copy(step = current - 1) }
                    } else {
                        _events.send(AllocationUiEvent.NavigateBack)
                    }
                }
                is AllocationUiAction.OnIncomeChanged -> {
                    _state.update { state ->
                        state.copy(income = action.value, remaining = computeRemaining(action.value, state.groups))
                    }
                }
                is AllocationUiAction.OnTemplateSelected -> {
                    _state.update { it.copy(selectedTemplate = action.template) }
                }
                is AllocationUiAction.OnEnvelopeAmountChanged -> {
                    _state.update { state ->
                        val newGroups = state.groups.map { group ->
                            group.copy(
                                envelopes = group.envelopes.map { env ->
                                    if (env.id == action.id) env.copy(amount = action.amount) else env
                                }
                            )
                        }
                        state.copy(groups = newGroups, remaining = computeRemaining(state.income, newGroups))
                    }
                }
            }
        }
    }
}
