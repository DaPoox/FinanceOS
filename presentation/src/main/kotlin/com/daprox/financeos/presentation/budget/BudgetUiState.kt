package com.daprox.financeos.presentation.budget

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCardUiState
import com.daprox.financeos.presentation.budget.component.fixessummary.FixesSummaryUiState
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState

@Stable
data class BudgetUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val monthLabel: String = "Mai 2026",
    val globalCard: BudgetGlobalCardUiState = BudgetGlobalCardUiState(),
    // Fixed charges shown in their own collapsible card, separate from the regular group list
    val fixesSummary: FixesSummaryUiState = FixesSummaryUiState(),
    val groups: List<BudgetEnvelopeGroup> = emptyList(),
    val expenseEnvelopes: List<EnvelopeChipUiState> = emptyList(),
    val isExpenseSheetVisible: Boolean = false,
    val isSaving: Boolean = false,
)
