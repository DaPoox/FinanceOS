package com.daprox.financeos.presentation.budget

/**
 * User actions dispatched from Budget screen.
 *
 * @property OnAllouerClick allocate/distribute month CTA clicked
 * @property OnEnvelopeClick specific envelope tapped, includes envelope id
 * @property OnAddExpenseClick add expense FAB clicked
 * @property OnExpenseDismiss expense form bottom sheet dismissed
 * @property OnExpenseSave save expense transaction with amount, envelope, note
 * @property OnRetry retry data load after error
 * @property OnFixesClick fixed charges card or header clicked
 */
sealed interface BudgetUiAction {
    data object OnAllouerClick : BudgetUiAction
    data class OnEnvelopeClick(val id: String) : BudgetUiAction
    data object OnAddExpenseClick : BudgetUiAction
    data object OnExpenseDismiss : BudgetUiAction
    data class OnExpenseSave(val amount: Double, val envelopeId: String, val note: String) : BudgetUiAction
    data object OnRetry : BudgetUiAction
    data object OnFixesClick : BudgetUiAction
}
