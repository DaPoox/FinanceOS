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
import com.daprox.financeos.presentation.core.designsystem.component.FinanceOSBottomNav
import com.daprox.financeos.presentation.dashboard.navigation.Dashboard
import com.daprox.financeos.presentation.dashboard.navigation.dashboardScreen
import com.daprox.financeos.presentation.envelopes.navigation.Envelopes
import com.daprox.financeos.presentation.envelopes.navigation.envelopesScreen
import com.daprox.financeos.presentation.navigation.AppTab

// Root nav graph.
// The shared Scaffold with a single FinanceOSBottomNav lives here so it persists
// across all destinations — individual screens never own the bottom bar.
// NavController never leaks into composables; only lambdas are passed down.
@Composable
fun AppNavGraph() {
    val navController      = rememberNavController()
    val navBackStackEntry  by navController.currentBackStackEntryAsState()
    val currentDest        = navBackStackEntry?.destination

    // Derive the selected tab index from the active back-stack destination.
    val selectedIndex = when {
        currentDest?.hasRoute<Envelopes>() == true -> AppTab.ENVELOPES.ordinal
        else                                        -> AppTab.WEALTH.ordinal
    }

    Scaffold(
        bottomBar = {
            FinanceOSBottomNav(
                items          = AppTab.toBottomNavItems(),
                selectedIndex  = selectedIndex,
                onItemSelected = { index ->
                    when (AppTab.entries[index]) {
                        AppTab.WEALTH    -> navController.navigate(Dashboard)  { launchSingleTop = true }
                        AppTab.ENVELOPES -> navController.navigate(Envelopes) { launchSingleTop = true }
                        AppTab.GROWTH    -> Unit // screen not built yet
                        AppTab.VAULT     -> Unit // screen not built yet
                    }
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Dashboard,
            modifier         = Modifier.padding(innerPadding),
        ) {
            dashboardScreen(
                // "Envelopes →" shortcut in the budget section header.
                onNavigateToEnvelopes = { navController.navigate(Envelopes) { launchSingleTop = true } },
            )
            envelopesScreen()
        }
    }
}
