# ViewModels & UiState — dump complet

---

## AllocationViewModel.kt

```kotlin
package com.daprox.financeos.presentation.allocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val TYPE_LABELS = mapOf(
    EnvelopeTypeEnum.FIXED to "Fixes",
    EnvelopeTypeEnum.VARIABLE to "Variables",
    EnvelopeTypeEnum.MONTHLY to "Du mois",
    EnvelopeTypeEnum.PERMANENT to "Permanentes",
    EnvelopeTypeEnum.SAVINGS to "Épargne",
    EnvelopeTypeEnum.INVESTMENT to "Investissement",
)

private val MOCK_ENVELOPES = listOf(
    AllocationEnvelopeUiState("loyer", "Loyer", Lucide.House, EnvelopeTypeEnum.FIXED, "900"),
    AllocationEnvelopeUiState("courses", "Courses", Lucide.ShoppingCart, EnvelopeTypeEnum.VARIABLE, "420"),
    AllocationEnvelopeUiState("restos", "Restos", Lucide.Utensils, EnvelopeTypeEnum.VARIABLE, "120"),
    AllocationEnvelopeUiState("voyage", "Voyage été", Lucide.Plane, EnvelopeTypeEnum.MONTHLY, "400"),
    AllocationEnvelopeUiState("fonds-urgence", "Fonds urgence", Lucide.TrendingUp, EnvelopeTypeEnum.PERMANENT, "200"),
    AllocationEnvelopeUiState("epargne", "Épargne", Lucide.Wallet, EnvelopeTypeEnum.SAVINGS, "500"),
    AllocationEnvelopeUiState("etf", "ETF World", Lucide.ChartBar, EnvelopeTypeEnum.INVESTMENT, "300"),
)

private fun List<AllocationEnvelopeUiState>.toGroups(): List<AllocationEnvelopeGroup> =
    TYPE_LABELS.entries.mapNotNull { (type, label) ->
        val items = filter { it.type == type }
        if (items.isEmpty()) null else AllocationEnvelopeGroup(label, items)
    }

private fun computeRemaining(income: String, groups: List<AllocationEnvelopeGroup>): Double {
    val incomeVal = income.toDoubleOrNull() ?: 0.0
    val totalAlloc = groups.flatMap { it.envelopes }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    return incomeVal - totalAlloc
}

class AllocationViewModel : ViewModel() {

    private val initialGroups = MOCK_ENVELOPES.toGroups()

    private val _state = MutableStateFlow(
        AllocationUiState(
            groups = initialGroups,
            remaining = computeRemaining("4200", initialGroups),
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<AllocationUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: AllocationUiAction) {
        viewModelScope.launch {
            when (action) {
                is AllocationUiAction.OnNext -> {
                    val current = _state.value.step
                    if (current < 2) {
                        _state.update { it.copy(step = current + 1) }
                    } else {
                        _events.send(AllocationUiEvent.NavigateBack)
                    }
                }
                is AllocationUiAction.OnBack -> {
                    val current = _state.value.step
                    if (current > 0) {
                        _state.update { it.copy(step = current - 1) }
                    } else {
                        _events.send(AllocationUiEvent.NavigateBack)
                    }
                }
                is AllocationUiAction.OnIncomeChanged -> {
                    _state.update { state ->
                        val newIncome = action.value
                        state.copy(
                            income = newIncome,
                            remaining = computeRemaining(newIncome, state.groups),
                        )
                    }
                }
                is AllocationUiAction.OnTemplateSelected -> {
                    _state.update { it.copy(selectedTemplate = action.template) }
                }
                is AllocationUiAction.OnEnvelopeAmountChanged -> {
                    _state.update { state ->
                        val newGroups = state.groups.map { group ->
                            group.copy(
                                envelopes = group.envelopes.map { env ->
                                    if (env.id == action.id) env.copy(amount = action.amount) else env
                                }
                            )
                        }
                        state.copy(
                            groups = newGroups,
                            remaining = computeRemaining(state.income, newGroups),
                        )
                    }
                }
            }
        }
    }
}
```

---

## AllocationUiState.kt

```kotlin
package com.daprox.financeos.presentation.allocation

import androidx.compose.runtime.Stable

@Stable
data class AllocationUiState(
    val step: Int = 0,
    val monthLabel: String = "Mai 2026",
    val income: String = "4200",
    val selectedTemplate: TemplateTypeEnum = TemplateTypeEnum.PREVIOUS,
    val groups: List<AllocationEnvelopeGroup> = emptyList(),
    val remaining: Double = 0.0,
)
```

---

## AllocationEnvelopeUiState.kt

```kotlin
package com.daprox.financeos.presentation.allocation

import androidx.compose.ui.graphics.vector.ImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

data class AllocationEnvelopeUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val type: EnvelopeTypeEnum,
    val amount: String,
)
```

---

## BudgetViewModel.kt

```kotlin
package com.daprox.financeos.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCardUiState
import com.daprox.financeos.presentation.budget.component.enveloperow.EnvelopeRowUiState
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val TYPE_LABELS = mapOf(
    EnvelopeTypeEnum.FIXED to "Fixes",
    EnvelopeTypeEnum.VARIABLE to "Variables",
    EnvelopeTypeEnum.MONTHLY to "Du mois",
    EnvelopeTypeEnum.PERMANENT to "Permanentes",
    EnvelopeTypeEnum.SAVINGS to "Épargne",
    EnvelopeTypeEnum.INVESTMENT to "Investissement",
)

private fun List<EnvelopeRowUiState>.toGroups(): List<BudgetEnvelopeGroup> =
    TYPE_LABELS.entries.mapNotNull { (type, label) ->
        val items = filter { it.type == type }
        if (items.isEmpty()) null else BudgetEnvelopeGroup(label, items)
    }

class BudgetViewModel : ViewModel() {

    private val mockEnvelopes = listOf(
        EnvelopeRowUiState(
            id = "loyer",
            name = "Loyer",
            icon = Lucide.House,
            type = EnvelopeTypeEnum.FIXED,
            spent = 900.0,
            allocated = 900.0,
            status = EnvelopeStatusEnum.FIXED,
            progress = 1f,
        ),
        EnvelopeRowUiState(
            id = "courses",
            name = "Courses",
            icon = Lucide.ShoppingCart,
            type = EnvelopeTypeEnum.VARIABLE,
            spent = 287.0,
            allocated = 420.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0.68f,
        ),
        EnvelopeRowUiState(
            id = "restos",
            name = "Restos",
            icon = Lucide.Utensils,
            type = EnvelopeTypeEnum.VARIABLE,
            spent = 134.0,
            allocated = 120.0,
            status = EnvelopeStatusEnum.OVER,
            progress = 1f,
        ),
        EnvelopeRowUiState(
            id = "voyage",
            name = "Voyage été",
            icon = Lucide.Plane,
            type = EnvelopeTypeEnum.MONTHLY,
            spent = 80.0,
            allocated = 400.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0.2f,
        ),
        EnvelopeRowUiState(
            id = "fonds-urgence",
            name = "Fonds urgence",
            icon = Lucide.TrendingUp,
            type = EnvelopeTypeEnum.PERMANENT,
            spent = 0.0,
            allocated = 200.0,
            accumulated = 1_400.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0f,
        ),
        EnvelopeRowUiState(
            id = "epargne",
            name = "Épargne",
            icon = Lucide.Wallet,
            type = EnvelopeTypeEnum.SAVINGS,
            spent = 300.0,
            allocated = 500.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0.6f,
        ),
        EnvelopeRowUiState(
            id = "etf",
            name = "ETF World",
            icon = Lucide.ChartBar,
            type = EnvelopeTypeEnum.INVESTMENT,
            spent = 150.0,
            allocated = 300.0,
            status = EnvelopeStatusEnum.OK,
            progress = 0.5f,
        ),
    )

    private val _state = MutableStateFlow(
        BudgetUiState(
            monthLabel = "Mai 2026",
            globalCard = BudgetGlobalCardUiState(
                income = 4200.0,
                totalSpent = mockEnvelopes.sumOf { it.spent },
                totalAllocated = mockEnvelopes.sumOf { it.allocated },
            ),
            groups = mockEnvelopes.toGroups(),
            expenseEnvelopes = mockEnvelopes.map { EnvelopeChipUiState(it.id, it.name, it.icon) },
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<BudgetUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: BudgetUiAction) {
        viewModelScope.launch {
            when (action) {
                is BudgetUiAction.OnAllouerClick -> _events.send(BudgetUiEvent.NavigateToAllocation)
                is BudgetUiAction.OnEnvelopeClick -> _events.send(BudgetUiEvent.NavigateToEnvelopeDetail(action.id))
                is BudgetUiAction.OnAddExpenseClick -> _state.update { it.copy(isExpenseSheetVisible = true) }
                is BudgetUiAction.OnExpenseDismiss -> _state.update { it.copy(isExpenseSheetVisible = false) }
                is BudgetUiAction.OnExpenseSave -> _state.update { it.copy(isExpenseSheetVisible = false) }
            }
        }
    }
}
```

---

## BudgetUiState.kt

```kotlin
package com.daprox.financeos.presentation.budget

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCardUiState
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState

@Stable
data class BudgetUiState(
    val monthLabel: String = "Mai 2026",
    val globalCard: BudgetGlobalCardUiState = BudgetGlobalCardUiState(),
    val groups: List<BudgetEnvelopeGroup> = emptyList(),
    val expenseEnvelopes: List<EnvelopeChipUiState> = emptyList(),
    val isExpenseSheetVisible: Boolean = false,
)
```

---

## BudgetGlobalCardUiState.kt

```kotlin
package com.daprox.financeos.presentation.budget.component.budgetglobalcard

data class BudgetGlobalCardUiState(
    val income: Double = 0.0,
    val totalSpent: Double = 0.0,
    val totalAllocated: Double = 0.0,
)
```

---

## EnvelopeRowUiState.kt

```kotlin
package com.daprox.financeos.presentation.budget.component.enveloperow

import androidx.compose.ui.graphics.vector.ImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

data class EnvelopeRowUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val type: EnvelopeTypeEnum,
    val spent: Double,
    val allocated: Double,
    val accumulated: Double = 0.0,
    val status: EnvelopeStatusEnum,
    val progress: Float,
)
```

---

## DashboardViewModel.kt

```kotlin
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
```

---

## DashboardUiState.kt

```kotlin
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
```

---

## BudgetMonthCardUiState.kt

```kotlin
package com.daprox.financeos.presentation.dashboard.component.budgetmonthcard

data class BudgetMonthCardUiState(
    val monthLabel: String = "",
    val income: Double = 0.0,
    val monthSpent: Double = 0.0,
    val monthAllocated: Double = 0.0,
    val isAllocated: Boolean = false,
)
```

---

## EnvelopeMiniUiState.kt

```kotlin
package com.daprox.financeos.presentation.dashboard.component.envelopeminigrid

import androidx.compose.ui.graphics.vector.ImageVector

data class EnvelopeMiniUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val type: EnvelopeTypeEnum,
    val spent: Double,
    val allocated: Double,
    val status: EnvelopeStatusEnum,
    val progress: Float,
)
```

---

## InsightCardUiState.kt

```kotlin
package com.daprox.financeos.presentation.dashboard.component.insightcard

data class InsightCardUiState(
    val type: InsightTypeEnum,
    val title: String,
    val subtitle: String,
    val highlightAmount: Double? = null,
)
```

---

## NetWorthHeroUiState.kt

```kotlin
package com.daprox.financeos.presentation.dashboard.component.networthhero

data class NetWorthHeroUiState(
    val netWorth: Double = 0.0,
    val delta: Double = 0.0,
    val insightLabel: String? = null,
    val contribSavings: Double = 0.0,
    val contribInvest: Double = 0.0,
)
```

---

## RecentMonthUiState.kt

```kotlin
package com.daprox.financeos.presentation.dashboard.component.recentmonthssection

data class RecentMonthUiState(
    val id: String,
    val monthLabel: String,
    val revenueLabel: String,
    val contribAmountLabel: String,
    val contribLabel: String,
    val status: MonthStatusEnum,
)
```

---

## SparklineCardUiState.kt

```kotlin
package com.daprox.financeos.presentation.dashboard.component.sparklinecard

data class SparklineCardUiState(
    val data: List<Double>,
    val monthLabels: List<String>,
    val pctLabel: String,
    val trend: SparklineTrendEnum,
)
```

---

## EnvelopeDetailViewModel.kt

```kotlin
package com.daprox.financeos.presentation.envelopedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private val TYPE_LABELS = mapOf(
    EnvelopeTypeEnum.FIXED to "Enveloppe fixe",
    EnvelopeTypeEnum.VARIABLE to "Variable standard",
    EnvelopeTypeEnum.MONTHLY to "Enveloppe du mois",
    EnvelopeTypeEnum.PERMANENT to "Permanente • accumulée",
    EnvelopeTypeEnum.SAVINGS to "Épargne",
    EnvelopeTypeEnum.INVESTMENT to "Investissement",
)

private val MOCK_ENVELOPES = mapOf(
    "loyer" to EnvelopeDetailUiState(
        id = "loyer",
        name = "Loyer",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.FIXED]!!,
        type = EnvelopeTypeEnum.FIXED,
        spent = 900.0,
        allocated = 900.0,
        status = EnvelopeStatusEnum.FIXED,
        transactions = listOf(
            TransactionUiState("t1", "Virement loyer", "1 mai", 900.0),
        ),
    ),
    "courses" to EnvelopeDetailUiState(
        id = "courses",
        name = "Courses",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.VARIABLE]!!,
        type = EnvelopeTypeEnum.VARIABLE,
        spent = 287.0,
        allocated = 420.0,
        status = EnvelopeStatusEnum.OK,
        transactions = listOf(
            TransactionUiState("t2", "Lidl", "12 mai", 42.0),
            TransactionUiState("t3", "Carrefour", "8 mai", 95.0),
            TransactionUiState("t4", "Biocoop", "5 mai", 67.0),
            TransactionUiState("t5", "Marché", "3 mai", 83.0),
        ),
    ),
    "restos" to EnvelopeDetailUiState(
        id = "restos",
        name = "Restos",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.VARIABLE]!!,
        type = EnvelopeTypeEnum.VARIABLE,
        spent = 134.0,
        allocated = 120.0,
        status = EnvelopeStatusEnum.OVER,
        transactions = listOf(
            TransactionUiState("t6", "Sushi House", "15 mai", 38.0),
            TransactionUiState("t7", "Brasserie du Nord", "9 mai", 52.0),
            TransactionUiState("t8", "McDonald's", "2 mai", 14.0),
            TransactionUiState("t9", "Pizzeria Napoli", "1 mai", 30.0),
        ),
    ),
    "voyage" to EnvelopeDetailUiState(
        id = "voyage",
        name = "Voyage été",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.MONTHLY]!!,
        type = EnvelopeTypeEnum.MONTHLY,
        spent = 80.0,
        allocated = 400.0,
        status = EnvelopeStatusEnum.OK,
        transactions = listOf(
            TransactionUiState("t10", "Booking hôtel", "10 mai", 80.0),
        ),
    ),
    "fonds-urgence" to EnvelopeDetailUiState(
        id = "fonds-urgence",
        name = "Fonds urgence",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.PERMANENT]!!,
        type = EnvelopeTypeEnum.PERMANENT,
        spent = 0.0,
        allocated = 200.0,
        accumulated = 1_400.0,
        status = EnvelopeStatusEnum.OK,
        transactions = emptyList(),
        monthlyHistory = listOf(200.0, 400.0, 600.0, 800.0, 1000.0, 1200.0, 1400.0),
        monthsAgo = 7,
    ),
    "epargne" to EnvelopeDetailUiState(
        id = "epargne",
        name = "Épargne",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.SAVINGS]!!,
        type = EnvelopeTypeEnum.SAVINGS,
        spent = 300.0,
        allocated = 500.0,
        status = EnvelopeStatusEnum.OK,
        transactions = listOf(
            TransactionUiState("t11", "Virement Livret A", "5 mai", 300.0),
        ),
    ),
    "etf" to EnvelopeDetailUiState(
        id = "etf",
        name = "ETF World",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.INVESTMENT]!!,
        type = EnvelopeTypeEnum.INVESTMENT,
        spent = 150.0,
        allocated = 300.0,
        status = EnvelopeStatusEnum.OK,
        transactions = listOf(
            TransactionUiState("t12", "Achat ETF MSCI World", "6 mai", 150.0),
        ),
    ),
)

class EnvelopeDetailViewModel(private val id: String) : ViewModel() {

    private val _state = MutableStateFlow(
        MOCK_ENVELOPES[id] ?: EnvelopeDetailUiState(id = id, name = id)
    )
    val state = _state.asStateFlow()

    private val _events = Channel<EnvelopeDetailUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: EnvelopeDetailUiAction) {
        viewModelScope.launch {
            when (action) {
                is EnvelopeDetailUiAction.OnBackClick -> _events.send(EnvelopeDetailUiEvent.NavigateBack)
                is EnvelopeDetailUiAction.OnModifierAllocationClick -> Unit
            }
        }
    }
}
```

---

## EnvelopeDetailUiState.kt

```kotlin
package com.daprox.financeos.presentation.envelopedetail

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

@Stable
data class EnvelopeDetailUiState(
    val id: String = "",
    val name: String = "",
    val typeLabel: String = "",
    val type: EnvelopeTypeEnum = EnvelopeTypeEnum.VARIABLE,
    val spent: Double = 0.0,
    val allocated: Double = 0.0,
    val accumulated: Double = 0.0,
    val status: EnvelopeStatusEnum = EnvelopeStatusEnum.OK,
    val transactions: List<TransactionUiState> = emptyList(),
    val monthlyHistory: List<Double> = emptyList(),
    val monthsAgo: Int = 0,
)
```

---

## TransactionUiState.kt

```kotlin
package com.daprox.financeos.presentation.envelopedetail

data class TransactionUiState(
    val id: String,
    val note: String,
    val dateLabel: String,
    val amount: Double,
)
```

---

## EnvelopesViewModel.kt

```kotlin
package com.daprox.financeos.presentation.envelopes

import androidx.lifecycle.ViewModel
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory
import com.daprox.financeos.presentation.envelopes.model.EnvelopeUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EnvelopesViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        EnvelopesUiState(
            totalFormattedAmount = "8 400,00 €",
            envelopes = listOf(
                EnvelopeUi(
                    id = "fixed",
                    name = "Fixed Expenses",
                    subtitle = "Rent, utilities, insurance",
                    formattedAmount = "2 940,00 €",
                    formattedPercentage = "35 %",
                    fraction = 0.35f,
                    category = EnvelopeCategory.FIXED_EXPENSES,
                    children = listOf(
                        EnvelopeUi("fixed_rent", "Rent", "Monthly lease", "1 200,00 €", "14 %", 0.14f, EnvelopeCategory.FIXED_EXPENSES),
                        EnvelopeUi("fixed_utilities", "Utilities", "Electricity, water", "180,00 €", "2 %", 0.02f, EnvelopeCategory.FIXED_EXPENSES),
                        EnvelopeUi("fixed_insurance", "Insurance", "Home + health", "160,00 €", "2 %", 0.02f, EnvelopeCategory.FIXED_EXPENSES),
                    ),
                ),
                EnvelopeUi(
                    id = "invest",
                    name = "Investment",
                    subtitle = "ETF, stocks, crypto",
                    formattedAmount = "1 680,00 €",
                    formattedPercentage = "20 %",
                    fraction = 0.20f,
                    category = EnvelopeCategory.INVESTMENT,
                    children = listOf(
                        EnvelopeUi("invest_etf", "ETF", "World index funds", "1 000,00 €", "12 %", 0.12f, EnvelopeCategory.INVESTMENT),
                        EnvelopeUi("invest_crypto", "Crypto", "BTC, ETH", "680,00 €", "8 %", 0.08f, EnvelopeCategory.INVESTMENT),
                    ),
                ),
                EnvelopeUi(
                    id = "savings",
                    name = "Savings",
                    subtitle = "Emergency fund, projects",
                    formattedAmount = "1 260,00 €",
                    formattedPercentage = "15 %",
                    fraction = 0.15f,
                    category = EnvelopeCategory.SAVINGS,
                    children = listOf(
                        EnvelopeUi("savings_emergency", "Emergency Fund", "3-month buffer", "840,00 €", "10 %", 0.10f, EnvelopeCategory.SAVINGS),
                        EnvelopeUi("savings_projects", "Projects", "Travel, gear", "420,00 €", "5 %", 0.05f, EnvelopeCategory.SAVINGS),
                    ),
                ),
                EnvelopeUi(
                    id = "other",
                    name = "Other",
                    subtitle = "Food, leisure, misc",
                    formattedAmount = "2 520,00 €",
                    formattedPercentage = "30 %",
                    fraction = 0.30f,
                    category = EnvelopeCategory.OTHER,
                    children = listOf(
                        EnvelopeUi("other_food", "Food", "Groceries, restaurants", "900,00 €", "11 %", 0.11f, EnvelopeCategory.OTHER),
                        EnvelopeUi("other_leisure", "Leisure", "Subscriptions, outings", "720,00 €", "9 %", 0.09f, EnvelopeCategory.OTHER),
                        EnvelopeUi("other_misc", "Misc", "Uncategorised", "900,00 €", "11 %", 0.10f, EnvelopeCategory.OTHER),
                    ),
                ),
            ),
        )
    )
    val state = _state.asStateFlow()

    fun onAction(action: EnvelopesUiAction) {
        when (action) {
            is EnvelopesUiAction.OnEnvelopeClick -> _state.update { current ->
                val newExpanded = if (current.expandedId == action.id) null else action.id
                current.copy(expandedId = newExpanded)
            }
            is EnvelopesUiAction.OnAddAllocationClick -> Unit
        }
    }
}
```

---

## EnvelopesUiState.kt

```kotlin
package com.daprox.financeos.presentation.envelopes

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.envelopes.model.EnvelopeUi

@Stable
data class EnvelopesUiState(
    val totalFormattedAmount: String = "0,00 €",
    val envelopes: List<EnvelopeUi> = emptyList(),
    val expandedId: String? = null,
)
```

---

## EnvelopesEditViewModel.kt

```kotlin
package com.daprox.financeos.presentation.envelopes.edit

import androidx.lifecycle.ViewModel
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

class EnvelopesEditViewModel : ViewModel() {

    private val totalIncomeRaw = 8_400.0

    private val _state = MutableStateFlow(
        EnvelopesEditUiState(
            monthLabel = "AVRIL 2026",
            totalIncome = "€8,400",
            items = initialItems(),
        ).withRecomputedSummary(totalIncomeRaw)
    )
    val state = _state.asStateFlow()

    fun onAction(action: EnvelopesEditUiAction) {
        when (action) {
            is EnvelopesEditUiAction.OnSliderChanged ->
                updateItem(action.id) { it.copy(fraction = action.fraction.coerceIn(0f, 1f)) }
            is EnvelopesEditUiAction.OnIncrement ->
                updateItem(action.id) { it.copy(fraction = (it.fraction + 0.01f).coerceAtMost(1f)) }
            is EnvelopesEditUiAction.OnDecrement ->
                updateItem(action.id) { it.copy(fraction = (it.fraction - 0.01f).coerceAtLeast(0f)) }
            is EnvelopesEditUiAction.OnConfirm -> Unit
            is EnvelopesEditUiAction.OnClose -> Unit
        }
    }

    private fun updateItem(id: String, transform: (EnvelopeEditItemUi) -> EnvelopeEditItemUi) {
        _state.update { current ->
            val updated = current.items.map { if (it.id == id) transform(it) else it }
            val recomputed = updated.map { it.withRecomputedDisplay(totalIncomeRaw) }
            current.copy(items = recomputed).withRecomputedSummary(totalIncomeRaw)
        }
    }

    private fun initialItems() = listOf(
        item("fixed", "Fixed", "Priorité Haute", EnvelopeCategory.FIXED_EXPENSES, 0.40f),
        item("investment", "Investment", "Croissance", EnvelopeCategory.INVESTMENT, 0.20f),
        item("savings", "Savings", "Réserve", EnvelopeCategory.SAVINGS, 0.15f),
        item("restaurants", "Restaurants", "Lifestyle", EnvelopeCategory.OTHER, 0.08f),
        item("loisirs", "Loisirs", "Plaisir", EnvelopeCategory.OTHER, 0.06f),
    )

    private fun item(id: String, name: String, subtitle: String, category: EnvelopeCategory, fraction: Float) =
        EnvelopeEditItemUi(
            id = id,
            name = name,
            subtitle = subtitle,
            category = category,
            fraction = fraction,
            formattedAmount = fraction.toFormattedAmount(totalIncomeRaw),
            formattedPercentage = fraction.toFormattedPercentage(),
        )
}

private fun EnvelopeEditItemUi.withRecomputedDisplay(totalIncome: Double) = copy(
    formattedAmount = fraction.toFormattedAmount(totalIncome),
    formattedPercentage = fraction.toFormattedPercentage(),
)

private fun EnvelopesEditUiState.withRecomputedSummary(totalIncome: Double): EnvelopesEditUiState {
    val totalFraction = items.sumOf { it.fraction.toDouble() }
    val allocatedPct = (totalFraction * 100).roundToInt()
    val allocatedEuros = (totalFraction.coerceAtMost(1.0) * totalIncome).roundToInt()
    val totalEuros = totalIncome.roundToInt()
    val isOver = allocatedPct > 100
    val isUnder = !isOver && allocatedPct < 100
    val overflowEuros = if (isOver) ((totalFraction - 1.0) * totalIncome).roundToInt() else 0
    val unallocatedEuros = if (isUnder) ((1.0 - totalFraction) * totalIncome).roundToInt() else 0
    val status = when {
        isOver -> "Dépassé"
        allocatedPct == 100 -> "Complet"
        allocatedPct >= 80 -> "Équilibré"
        else -> "Sous-alloué"
    }
    return copy(
        allocatedLabel = "$allocatedPct% Alloué",
        allocatedAmount = "€$allocatedEuros / €$totalEuros",
        statusLabel = status,
        isOverAllocated = isOver,
        overflowEurosLabel = if (isOver) "€$overflowEuros" else "",
        hasUnallocated = isUnder,
        unallocatedEurosLabel = if (isUnder) "€$unallocatedEuros" else "",
    )
}

private fun Float.toFormattedAmount(totalIncome: Double) = "€${(this * totalIncome).roundToInt()}"
private fun Float.toFormattedPercentage() = "${(this * 100).roundToInt()}%"
```

---

## EnvelopesEditUiState.kt

```kotlin
package com.daprox.financeos.presentation.envelopes.edit

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory

data class EnvelopeEditItemUi(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: EnvelopeCategory,
    val fraction: Float,
    val formattedAmount: String,
    val formattedPercentage: String,
)

@Stable
data class EnvelopesEditUiState(
    val monthLabel: String = "",
    val totalIncome: String = "",
    val allocatedLabel: String = "",
    val allocatedAmount: String = "",
    val statusLabel: String = "Équilibré",
    val items: List<EnvelopeEditItemUi> = emptyList(),
    val isOverAllocated: Boolean = false,
    val overflowEurosLabel: String = "",
    val hasUnallocated: Boolean = false,
    val unallocatedEurosLabel: String = "",
)
```

---

## EnvelopeChipUiState.kt

```kotlin
package com.daprox.financeos.presentation.expense

import androidx.compose.ui.graphics.vector.ImageVector

data class EnvelopeChipUiState(
    val id: String,
    val name: String,
    val icon: ImageVector,
)
```
