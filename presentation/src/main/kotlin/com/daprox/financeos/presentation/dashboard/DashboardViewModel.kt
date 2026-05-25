package com.daprox.financeos.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.domain.model.EnvelopeTypeEnum as DomainEnvelopeType
import com.daprox.financeos.domain.model.MonthStatusEnum as DomainMonthStatus
import com.daprox.financeos.domain.usecase.ObserveAccountsUseCase
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.ObserveCurrentMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthAllocationsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthTransactionsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthsUseCase
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private val SPARKLINE_6M = listOf(54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0)
private val SPARKLINE_6M_LABELS = listOf("déc", "jan", "fév", "mar", "avr", "mai")

private fun DomainEnvelopeType.toPresentation(): EnvelopeTypeEnum = EnvelopeTypeEnum.valueOf(name)

private fun DomainMonthStatus.toDashboardStatus(): MonthStatusEnum = when (this) {
    DomainMonthStatus.BEST, DomainMonthStatus.GOOD -> MonthStatusEnum.POSITIVE
    DomainMonthStatus.MID -> MonthStatusEnum.WARNING
    DomainMonthStatus.HARD -> MonthStatusEnum.NEGATIVE
}

class DashboardViewModel(
    observeCurrentMonth: ObserveCurrentMonthUseCase,
    observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    observeMonthAllocations: ObserveMonthAllocationsUseCase,
    observeMonthTransactions: ObserveMonthTransactionsUseCase,
    observeAccounts: ObserveAccountsUseCase,
    observeMonths: ObserveMonthsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<DashboardUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        observeCurrentMonth()
            .filterNotNull()
            .flatMapLatest { month ->
                combine(
                    observeActiveEnvelopes(),
                    observeMonthAllocations(month.id),
                    observeMonthTransactions(month.id),
                    observeAccounts(),
                    observeMonths(),
                ) { envelopes, allocations, transactions, accounts, months ->
                    val allocMap = allocations.associateBy { it.envelopeId }
                    val spendMap = transactions.groupBy { it.envelopeId }
                        .mapValues { (_, txs) -> txs.sumOf { it.amount } }

                    val netWorth = accounts.sumOf { it.balance }
                    val contribSavings = allocations
                        .filter { alloc -> envelopes.find { it.id == alloc.envelopeId }?.type == DomainEnvelopeType.SAVINGS }
                        .sumOf { it.allocated }
                    val contribInvest = allocations
                        .filter { alloc -> envelopes.find { it.id == alloc.envelopeId }?.type == DomainEnvelopeType.INVESTMENT }
                        .sumOf { it.allocated }

                    val insightLabel = when (month.status) {
                        DomainMonthStatus.BEST -> "Meilleur mois depuis 4 mois"
                        DomainMonthStatus.GOOD -> null
                        DomainMonthStatus.MID -> "Mois difficile — reste vigilant"
                        DomainMonthStatus.HARD -> "Dépassement du budget ce mois"
                    }

                    val expenseEnvelopes = envelopes.filter {
                        it.type !in listOf(DomainEnvelopeType.SAVINGS, DomainEnvelopeType.INVESTMENT)
                    }
                    val monthSpent = expenseEnvelopes.sumOf { spendMap[it.id] ?: 0.0 }
                    val monthAllocated = expenseEnvelopes.sumOf { allocMap[it.id]?.allocated ?: 0.0 }

                    val top4 = expenseEnvelopes
                        .sortedByDescending { spendMap[it.id] ?: 0.0 }
                        .take(4)
                        .map { env ->
                            val allocated = allocMap[env.id]?.allocated ?: 0.0
                            val spent = spendMap[env.id] ?: 0.0
                            val type = env.type.toPresentation()
                            val status = when {
                                type == EnvelopeTypeEnum.FIXED -> EnvelopeStatusEnum.FIXED
                                spent > allocated -> EnvelopeStatusEnum.OVER
                                allocated > 0 && spent / allocated > 0.8 -> EnvelopeStatusEnum.WARNING
                                else -> EnvelopeStatusEnum.OK
                            }
                            EnvelopeMiniUiState(
                                id = env.id,
                                name = env.name,
                                icon = iconKeyToImageVector(env.iconKey),
                                type = type,
                                spent = spent,
                                allocated = allocated,
                                status = status,
                                progress = if (allocated > 0) (spent / allocated).toFloat().coerceIn(0f, 1f) else 0f,
                            )
                        }

                    val worstEnvelope = envelopes
                        .filter { it.type == DomainEnvelopeType.VARIABLE || it.type == DomainEnvelopeType.MONTHLY }
                        .maxByOrNull { env ->
                            val alloc = allocMap[env.id]?.allocated ?: 0.0
                            val spent = spendMap[env.id] ?: 0.0
                            if (alloc > 0) spent / alloc else 0.0
                        }
                    val insight = worstEnvelope?.let { env ->
                        val alloc = allocMap[env.id]?.allocated ?: 0.0
                        val spent = spendMap[env.id] ?: 0.0
                        val ratio = if (alloc > 0) spent / alloc else 0.0
                        if (ratio > 0.5) {
                            val pct = (ratio * 100).toInt()
                            val remaining = (alloc - spent).coerceAtLeast(0.0)
                            InsightCardUiState(
                                type = if (ratio >= 1.0) InsightTypeEnum.ERROR else InsightTypeEnum.WARNING,
                                title = "${env.name} à $pct% du budget — ",
                                subtitle = "Il te reste ${remaining.frenchAmount()} € disponible.",
                            )
                        } else null
                    }

                    val recentMonths = months.drop(1).take(2).map { m ->
                        RecentMonthUiState(
                            id = m.id,
                            monthLabel = m.label,
                            revenueLabel = "Revenu ${m.income.frenchAmount()} €",
                            contribAmountLabel = if (m.contrib > 0) "+${m.contrib.frenchAmount()} €" else "—",
                            contribLabel = "ajouté au patrimoine",
                            status = m.status.toDashboardStatus(),
                        )
                    }

                    DashboardUiState(
                        netWorthHero = NetWorthHeroUiState(
                            netWorth = netWorth,
                            delta = contribSavings + contribInvest,
                            insightLabel = insightLabel,
                            contribSavings = contribSavings,
                            contribInvest = contribInvest,
                        ),
                        insight = insight,
                        budgetMonth = BudgetMonthCardUiState(
                            monthLabel = month.label.substringBefore(" "),
                            income = month.income,
                            monthSpent = monthSpent,
                            monthAllocated = monthAllocated,
                            isAllocated = month.isAllocated,
                        ),
                        envelopes = top4,
                        sparkline = SparklineCardUiState(
                            data = SPARKLINE_6M,
                            monthLabels = SPARKLINE_6M_LABELS,
                            pctLabel = "+11.8%",
                            trend = SparklineTrendEnum.POSITIVE,
                        ),
                        recentMonths = recentMonths,
                    )
                }
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

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
