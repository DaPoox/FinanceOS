package com.daprox.financeos.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daprox.financeos.presentation.core.designsystem.component.BudgetDonutSection
import com.daprox.financeos.presentation.core.designsystem.component.DashboardFab
import com.daprox.financeos.presentation.core.designsystem.component.DashboardTopBar
import com.daprox.financeos.presentation.core.designsystem.component.DualProgressBarsSection
import com.daprox.financeos.presentation.core.designsystem.component.FinanceOSBottomNav
import com.daprox.financeos.presentation.core.designsystem.component.LiquiditySection
import com.daprox.financeos.presentation.core.designsystem.component.NetWorthCard
import com.daprox.financeos.presentation.core.designsystem.component.RecentTransactionsSection
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
    Scaffold(
        topBar = { DashboardTopBar() },
        bottomBar = {
            FinanceOSBottomNav(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(DashboardUiAction.OnTabSelected(it)) },
            )
        },
        floatingActionButton = {
            DashboardFab(onClick = { onAction(DashboardUiAction.OnAddTransactionClick) })
        },
    ) { innerPadding ->
        // Content column — sections ordered top to bottom as per Figma.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            DualProgressBarsSection(progressBars = state.progressBars)

            NetWorthCard(
                netWorth = state.netWorth,
                modifier = Modifier.padding(top = 12.dp),
            )

            BudgetDonutSection(
                categories            = state.categories,
                totalFormattedAmount  = state.totalCategoryAmount,
                selectedCategoryIndex = state.selectedCategoryIndex,
                showAmounts           = state.showCategoryAmounts,
                onCategoryClick       = { onAction(DashboardUiAction.OnCategorySelected(it)) },
                modifier              = Modifier.padding(top = 12.dp),
            )

            LiquiditySection(
                items    = state.liquidityItems,
                modifier = Modifier.padding(top = 12.dp),
            )

            RecentTransactionsSection(
                transactions = state.recentTransactions,
                modifier     = Modifier.padding(top = 12.dp, bottom = 16.dp),
            )
        }
    }
}
