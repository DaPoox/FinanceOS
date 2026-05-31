package com.daprox.financeos.presentation.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.core.Result
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.domain.model.EnvelopeTypeEnum as DomainEnvelopeType
import com.daprox.financeos.domain.model.MonthStatusEnum as DomainMonthStatus
import com.daprox.financeos.domain.usecase.AddTransactionUseCase
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
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val SPARKLINE_6M = listOf(54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0)
private val SPARKLINE_6M_LABELS = listOf("déc", "jan", "fév", "mar", "avr", "mai")

private fun DomainEnvelopeType.toPresentation(): EnvelopeTypeEnum = EnvelopeTypeEnum.valueOf(name)

private fun DomainMonthStatus.toDashboardStatus(): MonthStatusEnum = when (this) {
    DomainMonthStatus.BEST, DomainMonthStatus.GOOD -> MonthStatusEnum.POSITIVE
    DomainMonthStatus.MID -> MonthStatusEnum.WARNING
    DomainMonthStatus.HARD -> MonthStatusEnum.NEGATIVE
}

/**
 * ViewModel for Dashboard screen. Drives net worth hero, budget month, envelopes, and trends.
 * Combines current month, envelopes, allocations, transactions, and accounts to render the dashboard.
 * Handles allocation of top 4 envelopes, insight generation, and recent month history.
 *
 * ## Init
 * Observes current month and related allocations, transactions, accounts in a complex combine flow.
 * Transforms domain models to UI models (e.g., DomainEnvelopeType -> EnvelopeTypeEnum).
 * Handles errors with retry trigger and emits loading/error/empty/content states.
 *
 * ## State
 * [state] emits DashboardUiState with net worth, insights, budget, envelopes, sparkline data.
 *
 * ## Actions
 * [onAction] routes user interactions: navigate to budget, allocate, envelope detail, month history, retry.
 *
 * ## Private Helpers
 * Domain type conversions, insight calculation based on worst-performing envelope,
 * recent months summary (last 2), sparkline data (hardcoded for 6M demo).
 */
class DashboardViewModel(
    private val observeCurrentMonth: ObserveCurrentMonthUseCase,
    private val observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    private val observeMonthAllocations: ObserveMonthAllocationsUseCase,
    private val observeMonthTransactions: ObserveMonthTransactionsUseCase,
    private val observeAccounts: ObserveAccountsUseCase,
    private val observeMonths: ObserveMonthsUseCase,
    private val addTransaction: AddTransactionUseCase,
) : ViewModel() {

    private var currentMonthId = ""
    private val _retryTrigger = MutableStateFlow(0)

    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<DashboardUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        // Observe the retry trigger to allow manual refreshing of the data flow
        _retryTrigger
            .flatMapLatest {
                observeCurrentMonth()
                    .flatMapLatest { month ->
                        // If no month data is found, display the empty state immediately
                        if (month == null) return@flatMapLatest flowOf(DashboardUiState(isLoading = false, isEmpty = true))
                        currentMonthId = month.id
                        combine(
                            observeActiveEnvelopes(),
                            observeMonthAllocations(month.id),
                            observeMonthTransactions(month.id),
                            observeAccounts(),
                            observeMonths(),
                        ) { envelopes, allocations, transactions, accounts, months ->
                            // Index data for efficient lookup during calculations
                            val allocMap = allocations.associateBy { it.envelopeId }
                            val spendMap = transactions.groupBy { it.envelopeId }
                                .mapValues { (_, txs) -> txs.sumOf { it.amount } }

                            // Calculate net worth based on current account balances
                            val netWorth = accounts.sumOf { it.balance }
                            // Sum contributions specifically for savings and investment envelopes
                            val contribSavings = allocations
                                .filter { alloc -> envelopes.find { it.id == alloc.envelopeId }?.type == DomainEnvelopeType.SAVINGS }
                                .sumOf { it.allocated }
                            val contribInvest = allocations
                                .filter { alloc -> envelopes.find { it.id == alloc.envelopeId }?.type == DomainEnvelopeType.INVESTMENT }
                                .sumOf { it.allocated }

                            // Determine the textual insight based on the current month's calculated status
                            val insightLabel = when (month.status) {
                                DomainMonthStatus.BEST -> "Meilleur mois depuis 4 mois"
                                DomainMonthStatus.GOOD -> null
                                DomainMonthStatus.MID -> "Mois difficile — reste vigilant"
                                DomainMonthStatus.HARD -> "Dépassement du budget ce mois"
                            }

                            // Filter out savings/investments to calculate actual spending budget
                            val expenseEnvelopes = envelopes.filter {
                                it.type !in listOf(DomainEnvelopeType.SAVINGS, DomainEnvelopeType.INVESTMENT)
                            }
                            val monthSpent = expenseEnvelopes.sumOf { spendMap[it.id] ?: 0.0 }
                            val monthAllocated = expenseEnvelopes.sumOf { allocMap[it.id]?.allocated ?: 0.0 }

                            // Select the top 4 envelopes by spending to display in the mini-grid
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

                            // Identify the envelope with the highest budget consumption (variable or monthly only)
                            val worstEnvelope = envelopes
                                .filter { it.type == DomainEnvelopeType.VARIABLE || it.type == DomainEnvelopeType.MONTHLY }
                                .maxByOrNull { env ->
                                    val alloc = allocMap[env.id]?.allocated ?: 0.0
                                    val spent = spendMap[env.id] ?: 0.0
                                    if (alloc > 0) spent / alloc else 0.0
                                }
                            // Generate an insight card if a problematic envelope is found (spent > 50%)
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

                            // Take the two most recent historical months for the history section
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

                            // Assemble the final UI state
                            DashboardUiState(
                                isLoading = false,
                                isEmpty = false,
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
                                expenseEnvelopes = envelopes.map { env ->
                                    EnvelopeChipUiState(env.id, env.name, iconKeyToImageVector(env.iconKey))
                                },
                            )
                        }
                    }
                    .catch { e ->
                        Log.e("DashboardViewModel", "Flow error", e)
                        // Emit error state to show retry UI to the user
                        emit(DashboardUiState(isLoading = false, isError = true))
                    }
            }
            .onEach { newState ->
                // Preserve transient UI state (expense sheet, saving indicator) on data refresh
                _state.update { current ->
                    newState.copy(
                        isExpenseSheetVisible = current.isExpenseSheetVisible,
                        isSaving = current.isSaving,
                    )
                }
            }
            // Keep the flow alive as long as the ViewModel is active
            .launchIn(viewModelScope)
    }

    /**
     * Handles user actions from Dashboard screen. Sends navigation events or updates state.
     *
     * @param action user action from UI
     */
    fun onAction(action: DashboardUiAction) {
        viewModelScope.launch {
            when (action) {
                is DashboardUiAction.OnBudgetMonthClick -> _events.send(DashboardUiEvent.NavigateToBudget)
                is DashboardUiAction.OnAllocateBudgetClick -> _events.send(DashboardUiEvent.NavigateToAllocation)
                is DashboardUiAction.OnSeeAllEnvelopesClick -> _events.send(DashboardUiEvent.NavigateToBudget)
                is DashboardUiAction.OnEnvelopeClick -> _events.send(DashboardUiEvent.NavigateToEnvelopeDetail(action.id))
                is DashboardUiAction.OnRecentMonthClick -> _events.send(DashboardUiEvent.NavigateToMonthHistory(action.id))
                is DashboardUiAction.OnRetry -> {
                    _state.update { it.copy(isLoading = true, isError = false) }
                    _retryTrigger.update { it + 1 }
                }
                is DashboardUiAction.OnAddExpenseClick -> _state.update { it.copy(isExpenseSheetVisible = true) }
                is DashboardUiAction.OnExpenseDismiss -> _state.update { it.copy(isExpenseSheetVisible = false) }
                is DashboardUiAction.OnExpenseSave -> {
                    _state.update { it.copy(isSaving = true) }
                    when (addTransaction(action.envelopeId, currentMonthId, action.amount, action.note)) {
                        is Result.Success -> _state.update { it.copy(isSaving = false, isExpenseSheetVisible = false) }
                        is Result.Error -> _state.update { it.copy(isSaving = false) }
                    }
                }
            }
        }
    }
}
