package com.daprox.financeos.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Utensils
import com.daprox.financeos.presentation.core.ObserveAsEvents
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.dashboard.component.budgetmonthcard.BudgetMonthCard
import com.daprox.financeos.presentation.dashboard.component.budgetmonthcard.BudgetMonthCardUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeMiniGrid
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeMiniUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import com.daprox.financeos.presentation.dashboard.component.insightcard.InsightCard
import com.daprox.financeos.presentation.dashboard.component.insightcard.InsightCardUiState
import com.daprox.financeos.presentation.dashboard.component.insightcard.InsightTypeEnum
import com.daprox.financeos.presentation.dashboard.component.networthhero.NetWorthHeroCard
import com.daprox.financeos.presentation.dashboard.component.networthhero.NetWorthHeroUiState
import com.daprox.financeos.presentation.dashboard.component.recentmonthssection.MonthStatusEnum
import com.daprox.financeos.presentation.dashboard.component.recentmonthssection.RecentMonthUiState
import com.daprox.financeos.presentation.dashboard.component.recentmonthssection.RecentMonthsSection
import com.daprox.financeos.presentation.dashboard.component.sparklinecard.SparklineCard
import com.daprox.financeos.presentation.dashboard.component.sparklinecard.SparklineCardUiState
import com.daprox.financeos.presentation.dashboard.component.sparklinecard.SparklineTrendEnum
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigateToBudget: () -> Unit = {},
    onNavigateToAllocation: () -> Unit = {},
    onNavigateToEnvelopeDetail: (String) -> Unit = {},
    onNavigateToMonthHistory: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is DashboardUiEvent.NavigateToBudget -> onNavigateToBudget()
            is DashboardUiEvent.NavigateToAllocation -> onNavigateToAllocation()
            is DashboardUiEvent.NavigateToEnvelopeDetail -> onNavigateToEnvelopeDetail(event.id)
            is DashboardUiEvent.NavigateToMonthHistory -> onNavigateToMonthHistory(event.id)
        }
    }

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onAction: (DashboardUiAction) -> Unit,
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp + navBarPadding.calculateBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { NetWorthHeroCard(state = state.netWorthHero) }

        item { InsightCard(state = state.insight) }

        item {
            BudgetMonthCard(
                state = state.budgetMonth,
                onTap = { onAction(DashboardUiAction.OnBudgetMonthClick) },
                onAllocateTap = { onAction(DashboardUiAction.OnAllocateBudgetClick) },
            )
        }

        if (state.envelopes.isNotEmpty()) {
            item {
                EnvelopeMiniGrid(
                    envelopes = state.envelopes,
                    onEnvelopeClick = { id -> onAction(DashboardUiAction.OnEnvelopeClick(id)) },
                    onSeeAllClick = { onAction(DashboardUiAction.OnSeeAllEnvelopesClick) },
                )
            }
        }

        item { SparklineCard(state = state.sparkline) }

        if (state.recentMonths.isNotEmpty()) {
            item {
                RecentMonthsSection(
                    months = state.recentMonths,
                    onMonthClick = { id -> onAction(DashboardUiAction.OnRecentMonthClick(id)) },
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun DashboardScreenPreview() {
    FinanceOSTheme {
        DashboardScreen(
            state = DashboardUiState(
                netWorthHero = NetWorthHeroUiState(
                    netWorth = 60580.0,
                    delta = 1840.0,
                    insightLabel = "Meilleur mois depuis 4 mois",
                    contribSavings = 600.0,
                    contribInvest = 650.0,
                    contribMarket = 590.0,
                ),
                insight = InsightCardUiState(
                    type = InsightTypeEnum.WARNING,
                    title = "Courses à 87% du budget — ",
                    subtitle = "Il te reste 54 € pour 12 jours.",
                ),
                budgetMonth = BudgetMonthCardUiState(
                    monthLabel = "Mai",
                    income = 4200.0,
                    monthSpent = 2374.0,
                    monthAllocated = 2720.0,
                    isAllocated = true,
                ),
                envelopes = listOf(
                    EnvelopeMiniUiState(
                        id = "courses", name = "Courses", icon = Lucide.ShoppingCart,
                        type = EnvelopeTypeEnum.VARIABLE, spent = 287.0, allocated = 420.0,
                        status = EnvelopeStatusEnum.OK, progress = 0.68f,
                    ),
                    EnvelopeMiniUiState(
                        id = "shopping", name = "Shopping", icon = Lucide.Car,
                        type = EnvelopeTypeEnum.VARIABLE, spent = 170.0, allocated = 200.0,
                        status = EnvelopeStatusEnum.WARNING, progress = 0.85f,
                    ),
                    EnvelopeMiniUiState(
                        id = "restos", name = "Restos", icon = Lucide.Utensils,
                        type = EnvelopeTypeEnum.VARIABLE, spent = 134.0, allocated = 120.0,
                        status = EnvelopeStatusEnum.OVER, progress = 1f,
                    ),
                    EnvelopeMiniUiState(
                        id = "loyer", name = "Loyer", icon = Lucide.House,
                        type = EnvelopeTypeEnum.FIXED, spent = 900.0, allocated = 900.0,
                        status = EnvelopeStatusEnum.FIXED, progress = 1f,
                    ),
                ),
                sparkline = SparklineCardUiState(
                    data = listOf(54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0),
                    monthLabels = listOf("déc", "jan", "fév", "mar", "avr", "mai"),
                    pctLabel = "+11.8%",
                    trend = SparklineTrendEnum.POSITIVE,
                ),
                recentMonths = listOf(
                    RecentMonthUiState(
                        id = "avr-2026", monthLabel = "Avril 2026",
                        revenueLabel = "Revenu 4 200 €", contribAmountLabel = "+1 840 €",
                        contribLabel = "ajouté au patrimoine", status = MonthStatusEnum.POSITIVE,
                    ),
                    RecentMonthUiState(
                        id = "mar-2026", monthLabel = "Mars 2026",
                        revenueLabel = "Revenu 3 800 €", contribAmountLabel = "+640 €",
                        contribLabel = "ajouté au patrimoine", status = MonthStatusEnum.WARNING,
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
