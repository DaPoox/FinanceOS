package com.daprox.financeos.presentation.dashboard

/**
 * User actions dispatched from Dashboard screen.
 *
 * @property OnBudgetMonthClick budget month card tapped
 * @property OnAllocateBudgetClick allocate budget CTA clicked
 * @property OnSeeAllEnvelopesClick see all envelopes clicked
 * @property OnEnvelopeClick specific envelope tapped, includes envelope id
 * @property OnRecentMonthClick recent month card tapped, includes month id
 * @property OnRetry retry data load after error
 */
sealed interface DashboardUiAction {
    data object OnBudgetMonthClick : DashboardUiAction
    data object OnAllocateBudgetClick : DashboardUiAction
    data object OnSeeAllEnvelopesClick : DashboardUiAction
    data class OnEnvelopeClick(val id: String) : DashboardUiAction
    data class OnRecentMonthClick(val id: String) : DashboardUiAction
    data object OnRetry : DashboardUiAction
}
