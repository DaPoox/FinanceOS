package com.daprox.financeos.presentation.dashboard

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.dashboard.component.budgetmonthcard.BudgetMonthCardUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeMiniUiState
import com.daprox.financeos.presentation.dashboard.component.insightcard.InsightCardUiState
import com.daprox.financeos.presentation.dashboard.component.networthhero.NetWorthHeroUiState
import com.daprox.financeos.presentation.dashboard.component.recentmonthssection.RecentMonthUiState
import com.daprox.financeos.presentation.dashboard.component.sparklinecard.SparklineCardUiState

/**
 * UI state for the Dashboard screen.
 *
 * @property isLoading true if data is loading
 * @property isError true if data load failed
 * @property isEmpty true if no month data exists (first launch)
 * @property netWorthHero net worth hero card state with total and contributions
 * @property insight optional insight card for budgeting alerts
 * @property budgetMonth current month budget summary
 * @property envelopes top 4 expense envelopes for quick view
 * @property sparkline net worth trend over 6 months
 * @property recentMonths 2 most recent months with status and contribution
 */
@Stable
data class DashboardUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val netWorthHero: NetWorthHeroUiState = NetWorthHeroUiState(),
    val insight: InsightCardUiState? = null,
    val budgetMonth: BudgetMonthCardUiState = BudgetMonthCardUiState(),
    val envelopes: List<EnvelopeMiniUiState> = emptyList(),
    val sparkline: SparklineCardUiState? = null,
    val recentMonths: List<RecentMonthUiState> = emptyList(),
)
