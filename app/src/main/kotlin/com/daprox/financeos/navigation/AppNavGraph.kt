package com.daprox.financeos.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.daprox.financeos.presentation.allocation.navigation.Allocation
import com.daprox.financeos.presentation.allocation.navigation.allocationScreen
import com.daprox.financeos.presentation.budget.navigation.Budget
import com.daprox.financeos.presentation.budget.navigation.budgetScreen
import com.daprox.financeos.presentation.core.designsystem.component.FinanceOSBottomNav
import com.daprox.financeos.presentation.dashboard.navigation.Dashboard
import com.daprox.financeos.presentation.dashboard.navigation.dashboardScreen
import com.daprox.financeos.presentation.envelopedetail.navigation.EnvelopeDetail
import com.daprox.financeos.presentation.envelopedetail.navigation.envelopeDetailScreen
import com.daprox.financeos.presentation.navigation.AppTab

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    val showBottomBar = currentDest?.let { dest ->
        dest.hasRoute<Dashboard>() == true || dest.hasRoute<Budget>() == true
    } ?: false

    val selectedIndex = when {
        currentDest?.hasRoute<Budget>() == true -> AppTab.BUDGET.ordinal
        currentDest?.hasRoute<EnvelopeDetail>() == true -> AppTab.BUDGET.ordinal
        else -> AppTab.HOME.ordinal
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FinanceOSBottomNav(
                    items = AppTab.toBottomNavItems(),
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        when (AppTab.entries[index]) {
                            AppTab.HOME -> navController.navigate(Dashboard) { launchSingleTop = true }
                            AppTab.BUDGET -> navController.navigate(Budget) { launchSingleTop = true }
                            AppTab.PATRIMOINE -> Unit // PatrimoineScreen not built yet
                            AppTab.HISTORIQUE -> Unit // HistoryScreen not built yet
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Dashboard,
            modifier = Modifier.padding(innerPadding),
        ) {
            dashboardScreen(
                onNavigateToBudget = { navController.navigate(Budget) { launchSingleTop = true } },
                onNavigateToAllocation = { navController.navigate(Allocation) },
                onNavigateToEnvelopeDetail = { id -> navController.navigate(EnvelopeDetail(id)) },
                onNavigateToMonthHistory = { /* HistoryScreen not built yet */ },
            )
            budgetScreen(
                onNavigateToAllocation = { navController.navigate(Allocation) },
                onNavigateToEnvelopeDetail = { id -> navController.navigate(EnvelopeDetail(id)) },
            )
            envelopeDetailScreen(
                onNavigateBack = { navController.popBackStack() },
            )
            allocationScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
