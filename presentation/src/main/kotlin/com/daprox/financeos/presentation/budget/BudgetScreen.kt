package com.daprox.financeos.presentation.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.daprox.financeos.presentation.core.designsystem.coloredShadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.SlidersHorizontal
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCard
import com.daprox.financeos.presentation.budget.component.budgetglobalcard.BudgetGlobalCardUiState
import com.daprox.financeos.presentation.budget.component.enveloperow.EnvelopeRow
import com.daprox.financeos.presentation.budget.component.enveloperow.EnvelopeRowUiState
import com.daprox.financeos.presentation.budget.component.fixessummary.FixesSummaryCard
import com.daprox.financeos.presentation.budget.component.fixessummary.FixesSummaryChargeUiState
import com.daprox.financeos.presentation.budget.component.fixessummary.FixesSummaryUiState
import com.daprox.financeos.presentation.core.ObserveAsEvents

import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import com.daprox.financeos.presentation.core.designsystem.component.EmptyStateView
import com.daprox.financeos.presentation.core.designsystem.component.ErrorStateView
import com.daprox.financeos.presentation.core.designsystem.component.ShimmerBox
import com.daprox.financeos.presentation.expense.EnvelopeChipUiState
import com.daprox.financeos.presentation.expense.ExpenseSheet
import org.koin.androidx.compose.koinViewModel


/**
 * Root composable for the Budget screen. Handles ViewModel initialization, state collection,
 * and event observation. Routes navigation events to appropriate callbacks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreenRoot(
    viewModel: BudgetViewModel = koinViewModel(),
    onNavigateToAllocation: () -> Unit = {},
    onNavigateToEnvelopeDetail: (String) -> Unit = {},
    onNavigateToFixes: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BudgetUiEvent.NavigateToAllocation -> onNavigateToAllocation()
            is BudgetUiEvent.NavigateToEnvelopeDetail -> onNavigateToEnvelopeDetail(event.id)
            is BudgetUiEvent.NavigateToFixes -> onNavigateToFixes()
        }
    }

    BudgetScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * Budget screen displaying global budget summary, envelope groups (FIXED separate),
 * and expense entry bottom sheet. Renders loading, error, empty, or full content based on state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    state: BudgetUiState,
    onAction: (BudgetUiAction) -> Unit,
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.isExpenseSheetVisible) {
        if (state.isExpenseSheetVisible) sheetState.show() else sheetState.hide()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(22.dp),
                    )
                    .padding(6.dp),
            ) {
                FloatingActionButton(
                    onClick = { onAction(BudgetUiAction.OnAddExpenseClick) },
                    modifier = Modifier.coloredShadow(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        borderRadius = 16.dp,
                        blurRadius = 24.dp,
                        offsetY = 8.dp,
                    ),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                ) {
                    Icon(imageVector = Lucide.Plus, contentDescription = "Nouvelle dépense")
                }
            }
        },
    ) { innerPadding ->
        val listPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + navBarPadding.calculateBottomPadding() + 8.dp,
        )

        when {
            state.isLoading -> BudgetScreenSkeleton(contentPadding = listPadding)
            state.isError -> ErrorStateView(
                onRetry = { onAction(BudgetUiAction.OnRetry) },
                modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            )
            state.isEmpty -> EmptyStateView(
                icon = Lucide.LayoutGrid,
                title = "Aucune enveloppe",
                subtitle = "Alloue ton premier mois pour créer tes enveloppes de budget.",
                ctaLabel = "Allouer le mois",
                onCta = { onAction(BudgetUiAction.OnAllouerClick) },
                modifier = Modifier.padding(innerPadding),
            )
            else -> LazyColumn(contentPadding = listPadding) {
                item {
                    BudgetHeader(
                        monthLabel = state.monthLabel,
                        onAllouerClick = { onAction(BudgetUiAction.OnAllouerClick) },
                    )
                }

                item {
                    BudgetGlobalCard(
                        state = state.globalCard,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                // Fixed charges get their own collapsible card instead of a standard group row
                if (state.fixesSummary.charges.isNotEmpty()) {
                    item(key = "fixes_summary") {
                        FixesSummaryCard(
                            state = state.fixesSummary,
                            onSeeDetail = { onAction(BudgetUiAction.OnFixesClick) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        )
                    }
                }

                state.groups.forEach { group ->
                    item(key = "label_${group.label}") {
                        BudgetSectionLabel(
                            label = group.label,
                            groupTotal = group.envelopes.sumOf { it.allocated },
                        )
                    }

                    itemsIndexed(group.envelopes, key = { _, e -> e.id }) { index, envelope ->
                        EnvelopeRow(
                            state = envelope,
                            onClick = { onAction(BudgetUiAction.OnEnvelopeClick(envelope.id)) },
                            animationDelayMs = index * 40,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp),
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }

    if (state.isExpenseSheetVisible) {
        ExpenseSheet(
            envelopes = state.expenseEnvelopes,
            sheetState = sheetState,
            onDismiss = { onAction(BudgetUiAction.OnExpenseDismiss) },
            onSave = { amount, id, note -> onAction(BudgetUiAction.OnExpenseSave(amount, id, note)) },
        )
    }
}

@Composable
private fun BudgetScreenSkeleton(contentPadding: PaddingValues) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 16.dp)) }
        repeat(4) {
            item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp)) }
        }
    }
}

@Composable
private fun BudgetHeader(
    monthLabel: String,
    onAllouerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.foundation.layout.Column {
            Text(
                text = "Budget",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = monthLabel,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Surface(
            onClick = onAllouerClick,
            shape = RoundedCornerShape(100.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Lucide.SlidersHorizontal,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = "Allouer",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun BudgetSectionLabel(
    label: String,
    groupTotal: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "${groupTotal.toLong().frenchAmount()} €",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMono),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetScreenPreview() {
    val envelopes = listOf(
        EnvelopeRowUiState(
            id = "loyer", name = "Loyer", icon = Lucide.House,
            type = EnvelopeTypeEnum.FIXED, spent = 900.0, allocated = 900.0,
            status = EnvelopeStatusEnum.FIXED, progress = 1f,
        ),
        EnvelopeRowUiState(
            id = "courses", name = "Courses", icon = Lucide.ShoppingCart,
            type = EnvelopeTypeEnum.VARIABLE, spent = 287.0, allocated = 420.0,
            status = EnvelopeStatusEnum.OK, progress = 0.68f,
        ),
        EnvelopeRowUiState(
            id = "restos", name = "Restos", icon = Lucide.Utensils,
            type = EnvelopeTypeEnum.VARIABLE, spent = 134.0, allocated = 120.0,
            status = EnvelopeStatusEnum.OVER, progress = 1f,
        ),
        EnvelopeRowUiState(
            id = "voyage", name = "Voyage été", icon = Lucide.Plane,
            type = EnvelopeTypeEnum.MONTHLY, spent = 80.0, allocated = 400.0,
            status = EnvelopeStatusEnum.OK, progress = 0.2f,
        ),
        EnvelopeRowUiState(
            id = "fonds", name = "Fonds urgence", icon = Lucide.TrendingUp,
            type = EnvelopeTypeEnum.PERMANENT, spent = 0.0, allocated = 200.0,
            accumulated = 1_400.0, status = EnvelopeStatusEnum.OK, progress = 0f,
        ),
        EnvelopeRowUiState(
            id = "epargne", name = "Épargne", icon = Lucide.Wallet,
            type = EnvelopeTypeEnum.SAVINGS, spent = 300.0, allocated = 500.0,
            status = EnvelopeStatusEnum.OK, progress = 0.6f,
        ),
        EnvelopeRowUiState(
            id = "etf", name = "ETF World", icon = Lucide.ChartBar,
            type = EnvelopeTypeEnum.INVESTMENT, spent = 150.0, allocated = 300.0,
            status = EnvelopeStatusEnum.OK, progress = 0.5f,
        ),
    )
    FinanceOSTheme {
        BudgetScreen(
            state = BudgetUiState(
                isLoading = false,
                monthLabel = "Mai 2026",
                globalCard = BudgetGlobalCardUiState(
                    income = 4200.0,
                    totalSpent = envelopes.sumOf { it.spent },
                    totalAllocated = envelopes.sumOf { it.allocated },
                ),
                groups = listOf(
                    BudgetEnvelopeGroup("Fixes", envelopes.filter { it.type == EnvelopeTypeEnum.FIXED }),
                    BudgetEnvelopeGroup("Variables", envelopes.filter { it.type == EnvelopeTypeEnum.VARIABLE }),
                    BudgetEnvelopeGroup("Du mois", envelopes.filter { it.type == EnvelopeTypeEnum.MONTHLY }),
                    BudgetEnvelopeGroup("Permanentes", envelopes.filter { it.type == EnvelopeTypeEnum.PERMANENT }),
                    BudgetEnvelopeGroup("Épargne", envelopes.filter { it.type == EnvelopeTypeEnum.SAVINGS }),
                    BudgetEnvelopeGroup("Investissement", envelopes.filter { it.type == EnvelopeTypeEnum.INVESTMENT }),
                ),
                expenseEnvelopes = envelopes.map { EnvelopeChipUiState(it.id, it.name, it.icon) },
            ),
            onAction = {},
        )
    }
}
