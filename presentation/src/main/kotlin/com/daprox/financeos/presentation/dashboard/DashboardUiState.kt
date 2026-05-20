package com.daprox.financeos.presentation.dashboard

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.dashboard.component.budgetmonthcard.BudgetMonthCardUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeMiniUiState
import com.daprox.financeos.presentation.dashboard.component.insightcard.InsightCardUiState
import com.daprox.financeos.presentation.dashboard.component.networthhero.NetWorthHeroUiState
import com.daprox.financeos.presentation.dashboard.component.recentmonthssection.RecentMonthUiState
import com.daprox.financeos.presentation.dashboard.component.sparklinecard.SparklineCardUiState

@Stable
data class DashboardUiState(
    val netWorthHero: NetWorthHeroUiState = NetWorthHeroUiState(),
    val insight: InsightCardUiState? = null,
    val budgetMonth: BudgetMonthCardUiState = BudgetMonthCardUiState(),
    val envelopes: List<EnvelopeMiniUiState> = emptyList(),
    val sparkline: SparklineCardUiState? = null,
    val recentMonths: List<RecentMonthUiState> = emptyList(),
)
