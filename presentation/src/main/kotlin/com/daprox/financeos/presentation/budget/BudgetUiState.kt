package com.daprox.financeos.presentation.budget

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCardUiState
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState

@Stable
data class BudgetUiState(
    val monthLabel: String = "Mai 2026",
    val globalCard: BudgetGlobalCardUiState = BudgetGlobalCardUiState(),
    val groups: List<BudgetEnvelopeGroup> = emptyList(),
    val expenseEnvelopes: List<EnvelopeChipUiState> = emptyList(),
    val isExpenseSheetVisible: Boolean = false,
    val isSaving: Boolean = false,
)
