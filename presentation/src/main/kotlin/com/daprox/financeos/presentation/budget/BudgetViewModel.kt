package com.daprox.financeos.presentation.budget

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
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCardUiState
import com.daprox.financeos.presentation.budget.component.enveloperow.EnvelopeRowUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState
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

private fun List<EnvelopeRowUiState>.toGroups(): List<BudgetEnvelopeGroup> =
    TYPE_LABELS.entries.mapNotNull { (type, label) ->
        val items = filter { it.type == type }
        if (items.isEmpty()) null else BudgetEnvelopeGroup(label, items)
    }

class BudgetViewModel : ViewModel() {

    private val mockEnvelopes = listOf(
        EnvelopeRowUiState(
            id = "loyer",
            name = "Loyer",
            icon = Lucide.House,
            type = EnvelopeTypeEnum.FIXED,
            spent = 900.0,
            allocated = 900.0,
            status = EnvelopeStatusEnum.FIXED,
            progress = 1f,
        ),
        EnvelopeRowUiState(
            id = "courses",
            name = "Courses",
            icon = Lucide.ShoppingCart,
            type = EnvelopeTypeEnum.VARIABLE,
            spent = 287.0,
            allocated = 420.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0.68f,
        ),
        EnvelopeRowUiState(
            id = "restos",
            name = "Restos",
            icon = Lucide.Utensils,
            type = EnvelopeTypeEnum.VARIABLE,
            spent = 134.0,
            allocated = 120.0,
            status = EnvelopeStatusEnum.OVER,
            progress = 1f,
        ),
        EnvelopeRowUiState(
            id = "voyage",
            name = "Voyage été",
            icon = Lucide.Plane,
            type = EnvelopeTypeEnum.MONTHLY,
            spent = 80.0,
            allocated = 400.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0.2f,
        ),
        EnvelopeRowUiState(
            id = "fonds-urgence",
            name = "Fonds urgence",
            icon = Lucide.TrendingUp,
            type = EnvelopeTypeEnum.PERMANENT,
            spent = 0.0,
            allocated = 200.0,
            accumulated = 1_400.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0f,
        ),
        EnvelopeRowUiState(
            id = "epargne",
            name = "Épargne",
            icon = Lucide.Wallet,
            type = EnvelopeTypeEnum.SAVINGS,
            spent = 300.0,
            allocated = 500.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0.6f,
        ),
        EnvelopeRowUiState(
            id = "etf",
            name = "ETF World",
            icon = Lucide.ChartBar,
            type = EnvelopeTypeEnum.INVESTMENT,
            spent = 150.0,
            allocated = 300.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0.5f,
        ),
    )

    private val _state = MutableStateFlow(
        BudgetUiState(
            monthLabel = "Mai 2026",
            globalCard = BudgetGlobalCardUiState(
                income = 4200.0,
                totalSpent = mockEnvelopes.sumOf { it.spent },
                totalAllocated = mockEnvelopes.sumOf { it.allocated },
            ),
            groups = mockEnvelopes.toGroups(),
            expenseEnvelopes = mockEnvelopes.map { EnvelopeChipUiState(it.id, it.name, it.icon) },
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<BudgetUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: BudgetUiAction) {
        viewModelScope.launch {
            when (action) {
                is BudgetUiAction.OnAllouerClick -> _events.send(BudgetUiEvent.NavigateToAllocation)
                is BudgetUiAction.OnEnvelopeClick -> _events.send(BudgetUiEvent.NavigateToEnvelopeDetail(action.id))
                is BudgetUiAction.OnAddExpenseClick -> _state.update { it.copy(isExpenseSheetVisible = true) }
                is BudgetUiAction.OnExpenseDismiss -> _state.update { it.copy(isExpenseSheetVisible = false) }
                is BudgetUiAction.OnExpenseSave -> _state.update { it.copy(isExpenseSheetVisible = false) }
            }
        }
    }
}
