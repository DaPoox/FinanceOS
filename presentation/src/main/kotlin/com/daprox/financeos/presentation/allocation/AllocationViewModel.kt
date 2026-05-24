package com.daprox.financeos.presentation.allocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

private val MOCK_ENVELOPES = listOf(
    AllocationEnvelopeUiState("loyer", "Loyer", Lucide.House, EnvelopeTypeEnum.FIXED, "900"),
    AllocationEnvelopeUiState("courses", "Courses", Lucide.ShoppingCart, EnvelopeTypeEnum.VARIABLE, "420"),
    AllocationEnvelopeUiState("restos", "Restos", Lucide.Utensils, EnvelopeTypeEnum.VARIABLE, "120"),
    AllocationEnvelopeUiState("voyage", "Voyage été", Lucide.Plane, EnvelopeTypeEnum.MONTHLY, "400"),
    AllocationEnvelopeUiState("fonds-urgence", "Fonds urgence", Lucide.TrendingUp, EnvelopeTypeEnum.PERMANENT, "200"),
    AllocationEnvelopeUiState("epargne", "Épargne", Lucide.Wallet, EnvelopeTypeEnum.SAVINGS, "500"),
    AllocationEnvelopeUiState("etf", "ETF World", Lucide.ChartBar, EnvelopeTypeEnum.INVESTMENT, "300"),
)

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

class AllocationViewModel : ViewModel() {

    private val initialGroups = MOCK_ENVELOPES.toGroups()

    private val _state = MutableStateFlow(
        AllocationUiState(
            groups = initialGroups,
            remaining = computeRemaining("4200", initialGroups),
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<AllocationUiEvent>()
    val events = _events.receiveAsFlow()

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
                        val newIncome = action.value
                        state.copy(
                            income = newIncome,
                            remaining = computeRemaining(newIncome, state.groups),
                        )
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
                        state.copy(
                            groups = newGroups,
                            remaining = computeRemaining(state.income, newGroups),
                        )
                    }
                }
            }
        }
    }
}
