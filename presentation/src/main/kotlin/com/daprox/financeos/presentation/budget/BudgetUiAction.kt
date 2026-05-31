package com.daprox.financeos.presentation.budget

import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

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
 * @property OnAddEnvelopeClick add envelope row tapped in a group, includes the preset type
 * @property OnNewEnvelopeDismiss new envelope sheet dismissed
 * @property OnNewEnvelopeSaved save new envelope from the sheet
 */
sealed interface BudgetUiAction {
    data object OnAllouerClick : BudgetUiAction
    data class OnEnvelopeClick(val id: String) : BudgetUiAction
    data object OnAddExpenseClick : BudgetUiAction
    data object OnExpenseDismiss : BudgetUiAction
    data class OnExpenseSave(val amount: Double, val envelopeId: String, val note: String) : BudgetUiAction
    data object OnRetry : BudgetUiAction
    data object OnFixesClick : BudgetUiAction
    data class OnAddEnvelopeClick(val type: EnvelopeTypeEnum) : BudgetUiAction
    data object OnNewEnvelopeDismiss : BudgetUiAction
    data class OnNewEnvelopeSaved(
        val name: String,
        val typeKey: String,
        val iconKey: String,
        val amount: Double,
    ) : BudgetUiAction
}
