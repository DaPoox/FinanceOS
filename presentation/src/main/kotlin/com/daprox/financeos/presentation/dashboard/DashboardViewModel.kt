package com.daprox.financeos.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Utensils
import com.daprox.financeos.presentation.dashboard.component.budgetmonthcard.BudgetMonthCardUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeMiniUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import com.daprox.financeos.presentation.dashboard.component.insightcard.InsightCardUiState
import com.daprox.financeos.presentation.dashboard.component.insightcard.InsightTypeEnum
import com.daprox.financeos.presentation.dashboard.component.networthhero.NetWorthHeroUiState
import com.daprox.financeos.presentation.dashboard.component.recentmonthssection.MonthStatusEnum
import com.daprox.financeos.presentation.dashboard.component.recentmonthssection.RecentMonthUiState
import com.daprox.financeos.presentation.dashboard.component.sparklinecard.SparklineCardUiState
import com.daprox.financeos.presentation.dashboard.component.sparklinecard.SparklineTrendEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        DashboardUiState(
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
                highlightAmount = null,
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
                    id = "courses",
                    name = "Courses",
                    icon = Lucide.ShoppingCart,
                    type = EnvelopeTypeEnum.VARIABLE,
                    spent = 287.0,
                    allocated = 420.0,
                    status = EnvelopeStatusEnum.OK,
                    progress = 0.68f,
                ),
                EnvelopeMiniUiState(
                    id = "shopping",
                    name = "Shopping",
                    icon = Lucide.Car,
                    type = EnvelopeTypeEnum.VARIABLE,
                    spent = 170.0,
                    allocated = 200.0,
                    status = EnvelopeStatusEnum.WARNING,
                    progress = 0.85f,
                ),
                EnvelopeMiniUiState(
                    id = "restos",
                    name = "Restos",
                    icon = Lucide.Utensils,
                    type = EnvelopeTypeEnum.VARIABLE,
                    spent = 134.0,
                    allocated = 120.0,
                    status = EnvelopeStatusEnum.OVER,
                    progress = 1f,
                ),
                EnvelopeMiniUiState(
                    id = "loyer",
                    name = "Loyer",
                    icon = Lucide.House,
                    type = EnvelopeTypeEnum.FIXED,
                    spent = 900.0,
                    allocated = 900.0,
                    status = EnvelopeStatusEnum.FIXED,
                    progress = 1f,
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
                    id = "avr-2026",
                    monthLabel = "Avril 2026",
                    revenueLabel = "Revenu 4 200 €",
                    contribAmountLabel = "+1 840 €",
                    contribLabel = "ajouté au patrimoine",
                    status = MonthStatusEnum.POSITIVE,
                ),
                RecentMonthUiState(
                    id = "mar-2026",
                    monthLabel = "Mars 2026",
                    revenueLabel = "Revenu 3 800 €",
                    contribAmountLabel = "+640 €",
                    contribLabel = "ajouté au patrimoine",
                    status = MonthStatusEnum.WARNING,
                ),
            ),
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<DashboardUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: DashboardUiAction) {
        viewModelScope.launch {
            when (action) {
                is DashboardUiAction.OnBudgetMonthClick -> _events.send(DashboardUiEvent.NavigateToBudget)
                is DashboardUiAction.OnAllocateBudgetClick -> _events.send(DashboardUiEvent.NavigateToAllocation)
                is DashboardUiAction.OnSeeAllEnvelopesClick -> _events.send(DashboardUiEvent.NavigateToBudget)
                is DashboardUiAction.OnEnvelopeClick -> _events.send(DashboardUiEvent.NavigateToEnvelopeDetail(action.id))
                is DashboardUiAction.OnRecentMonthClick -> _events.send(DashboardUiEvent.NavigateToMonthHistory(action.id))
            }
        }
    }
}
