package com.daprox.financeos.presentation.allocation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.model.EnvelopeTypeEnum as DomainEnvelopeType
import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.usecase.AddEnvelopeToMonthUseCase
import com.daprox.financeos.domain.usecase.AllocateMonthUseCase
import com.daprox.financeos.domain.usecase.CopyAllocationFromMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.ObserveCurrentMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthAllocationsUseCase
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
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

private fun previousMonthId(currentId: String): String {
    val parts = currentId.split("-")
    if (parts.size != 2) return currentId
    val year = parts[0].toIntOrNull() ?: return currentId
    val month = parts[1].toIntOrNull() ?: return currentId
    return if (month == 1) "${year - 1}-12" else "$year-${(month - 1).toString().padStart(2, '0')}"
}

private fun List<AllocationEnvelopeGroup>.toMonthAllocations(monthId: String): List<MonthAllocation> =
    flatMap { group ->
        group.envelopes.map { env ->
            MonthAllocation(
                monthId = monthId,
                envelopeId = env.id,
                allocated = env.amount.toDoubleOrNull() ?: 0.0,
                accumulated = 0.0,
            )
        }
    }

private fun AllocationUiState.withAllocationsApplied(allocations: List<MonthAllocation>): AllocationUiState {
    val allocMap = allocations.associateBy { it.envelopeId }
    val newGroups = groups.map { group ->
        group.copy(
            envelopes = group.envelopes.map { env ->
                val alloc = allocMap[env.id]
                if (alloc != null) env.copy(amount = alloc.allocated.toLong().toString()) else env
            },
        )
    }
    return copy(groups = newGroups, remaining = computeRemaining(income, newGroups))
}

private fun colorHexForType(type: DomainEnvelopeType): String = when (type) {
    DomainEnvelopeType.FIXED -> "#4a5568"
    DomainEnvelopeType.VARIABLE -> "#e8eef5"
    DomainEnvelopeType.MONTHLY -> "#fb923c"
    DomainEnvelopeType.PERMANENT -> "#22c55e"
    DomainEnvelopeType.SAVINGS -> "#7eb8f7"
    DomainEnvelopeType.INVESTMENT -> "#a78bfa"
}

class AllocationViewModel(
    private val observeCurrentMonth: ObserveCurrentMonthUseCase,
    private val observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    private val observeMonthAllocations: ObserveMonthAllocationsUseCase,
    private val allocateMonth: AllocateMonthUseCase,
    private val copyAllocation: CopyAllocationFromMonthUseCase,
    private val addEnvelopeToMonth: AddEnvelopeToMonthUseCase,
) : ViewModel() {

    private var currentMonthId = ""
    private val _retryTrigger = MutableStateFlow(0)

    private val _state = MutableStateFlow(AllocationUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<AllocationUiEvent>()
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
                                isLoading = false,
                                income = income,
                                groups = groups,
                                remaining = computeRemaining(income, groups),
                            )
                        }
                    }
                    .catch { e ->
                        Log.e("AllocationViewModel", "Flow error", e)
                        emit(AllocationUiState(isLoading = false, isError = true))
                    }
            }
            .onEach { newState ->
                _state.update { current ->
                    if (current.groups.isEmpty()) {
                        // First emission — take everything but preserve sheet/undo transient state
                        newState.copy(
                            lastRemovedEnvelope = current.lastRemovedEnvelope,
                            isNewEnvelopeSheetVisible = current.isNewEnvelopeSheetVisible,
                            newEnvelopePresetType = current.newEnvelopePresetType,
                        )
                    } else {
                        // Subsequent emissions — preserve user-edited amounts and filter out the
                        // locally-deleted envelope so the DB re-emission doesn't undo the swipe
                        val userAmounts = current.groups.flatMap { it.envelopes }.associate { it.id to it.amount }
                        val removedId = current.lastRemovedEnvelope?.id
                        val mergedGroups = newState.groups.map { group ->
                            group.copy(
                                envelopes = group.envelopes
                                    .filter { it.id != removedId }
                                    .map { env -> env.copy(amount = userAmounts[env.id] ?: env.amount) },
                            )
                        }.filter { it.envelopes.isNotEmpty() }
                        current.copy(
                            isLoading = newState.isLoading,
                            isError = newState.isError,
                            income = newState.income,
                            groups = mergedGroups,
                            remaining = computeRemaining(current.income, mergedGroups),
                        )
                    }
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
                        _state.update { it.copy(isSaving = true) }
                        val state = _state.value
                        val result = allocateMonth(
                            monthId = currentMonthId,
                            income = state.income.toDoubleOrNull() ?: 0.0,
                            allocations = state.groups.toMonthAllocations(currentMonthId),
                        )
                        when (result) {
                            is Result.Success -> _events.send(AllocationUiEvent.NavigateBack)
                            is Result.Error -> {
                                _state.update { it.copy(isSaving = false) }
                                _events.send(AllocationUiEvent.ShowError)
                            }
                        }
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

                is AllocationUiAction.OnIncomeChanged ->
                    _state.update { state ->
                        state.copy(income = action.value, remaining = computeRemaining(action.value, state.groups))
                    }

                is AllocationUiAction.OnTemplateSelected -> {
                    _state.update { it.copy(selectedTemplate = action.template) }
                    if (action.template == TemplateTypeEnum.PREVIOUS) {
                        val fromMonthId = previousMonthId(currentMonthId)
                        val allocations = copyAllocation(fromMonthId)
                        if (allocations.isNotEmpty()) {
                            _state.update { it.withAllocationsApplied(allocations) }
                        }
                    }
                }

                is AllocationUiAction.OnEnvelopeAmountChanged ->
                    _state.update { state ->
                        val newGroups = state.groups.map { group ->
                            group.copy(
                                envelopes = group.envelopes.map { env ->
                                    if (env.id == action.id) env.copy(amount = action.amount) else env
                                },
                            )
                        }
                        state.copy(groups = newGroups, remaining = computeRemaining(state.income, newGroups))
                    }

                is AllocationUiAction.OnRetry -> {
                    _state.update { it.copy(isLoading = true, isError = false) }
                    _retryTrigger.update { it + 1 }
                }

                // Remove envelope locally; the undo snackbar gives the user a chance to restore it
                is AllocationUiAction.OnEnvelopeDeleted ->
                    _state.update { state ->
                        val newGroups = state.groups.map { group ->
                            group.copy(envelopes = group.envelopes.filter { it.id != action.envelope.id })
                        }.filter { it.envelopes.isNotEmpty() }
                        state.copy(
                            groups = newGroups,
                            remaining = computeRemaining(state.income, newGroups),
                            lastRemovedEnvelope = action.envelope,
                        )
                    }

                // Re-insert the envelope into its correct group
                is AllocationUiAction.OnEnvelopeRestored -> {
                    val removed = _state.value.lastRemovedEnvelope ?: return@launch
                    _state.update { state ->
                        val targetLabel = TYPE_LABELS[removed.type] ?: return@update state
                        val newGroups = if (state.groups.any { it.label == targetLabel }) {
                            state.groups.map { group ->
                                if (group.label == targetLabel) {
                                    group.copy(envelopes = group.envelopes + removed)
                                } else group
                            }
                        } else {
                            // Group was fully removed — add it back in the correct order
                            (state.groups + AllocationEnvelopeGroup(targetLabel, listOf(removed)))
                                .sortedBy { g -> TYPE_LABELS.values.indexOf(g.label) }
                        }
                        state.copy(
                            groups = newGroups,
                            remaining = computeRemaining(state.income, newGroups),
                            lastRemovedEnvelope = null,
                        )
                    }
                }

                is AllocationUiAction.OnClearRemovedEnvelope ->
                    _state.update { it.copy(lastRemovedEnvelope = null) }

                is AllocationUiAction.OnAddEnvelopeClick ->
                    _state.update { it.copy(isNewEnvelopeSheetVisible = true, newEnvelopePresetType = action.typeKey) }

                is AllocationUiAction.OnNewEnvelopeDismiss ->
                    _state.update { it.copy(isNewEnvelopeSheetVisible = false, newEnvelopePresetType = null) }

                // Create the envelope and add it to the current month at the given amount
                is AllocationUiAction.OnNewEnvelopeSaved -> {
                    _state.update { it.copy(isNewEnvelopeSheetVisible = false, newEnvelopePresetType = null) }
                    val domainType = runCatching { DomainEnvelopeType.valueOf(action.typeKey) }.getOrNull()
                        ?: DomainEnvelopeType.VARIABLE
                    val envelope = Envelope(
                        id = "",
                        name = action.name,
                        type = domainType,
                        iconKey = action.iconKey,
                        colorHex = colorHexForType(domainType),
                        isActive = true,
                    )
                    addEnvelopeToMonth(envelope, currentMonthId, action.amount)
                    // DB flow emits; merge fix will pick up the new envelope with its saved amount
                }
            }
        }
    }
}
