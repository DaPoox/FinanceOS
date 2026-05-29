package com.daprox.financeos.presentation.budget

sealed interface BudgetUiAction {
    data object OnAllouerClick : BudgetUiAction
    data class OnEnvelopeClick(val id: String) : BudgetUiAction
    data object OnAddExpenseClick : BudgetUiAction
    data object OnExpenseDismiss : BudgetUiAction
    data class OnExpenseSave(val amount: Double, val envelopeId: String, val note: String) : BudgetUiAction
    data object OnRetry : BudgetUiAction
    data object OnFixesClick : BudgetUiAction
}
