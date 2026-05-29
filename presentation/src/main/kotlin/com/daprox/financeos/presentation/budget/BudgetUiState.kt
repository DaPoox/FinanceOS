package com.daprox.financeos.presentation.budget

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCardUiState
import com.daprox.financeos.presentation.budget.component.fixessummary.FixesSummaryUiState
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState

/**
 * UI state for the Budget screen.
 *
 * @property isLoading true if data is loading
 * @property isError true if data load failed
 * @property isEmpty true if no envelopes exist
 * @property monthLabel current month display label
 * @property globalCard budget summary (income, allocated, spent)
 * @property fixesSummary fixed charges card (collapsible, separate from groups)
 * @property groups envelope groups (VARIABLE, MONTHLY, etc.)
 * @property expenseEnvelopes all envelopes for expense sheet dropdown
 * @property isExpenseSheetVisible whether expense add-form bottom sheet is visible
 * @property isSaving whether transaction is being saved
 */
@Stable
data class BudgetUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val monthLabel: String = "Mai 2026",
    val globalCard: BudgetGlobalCardUiState = BudgetGlobalCardUiState(),
    val fixesSummary: FixesSummaryUiState = FixesSummaryUiState(),
    val groups: List<BudgetEnvelopeGroup> = emptyList(),
    val expenseEnvelopes: List<EnvelopeChipUiState> = emptyList(),
    val isExpenseSheetVisible: Boolean = false,
    val isSaving: Boolean = false,
)
