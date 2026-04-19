package com.daprox.financeos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.daprox.financeos.presentation.dashboard.navigation.Dashboard
import com.daprox.financeos.presentation.dashboard.navigation.dashboardScreen

// Root nav graph. Assembles all feature graphs and wires cross-feature navigation via lambdas.
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Dashboard,
    ) {
        dashboardScreen()
    }
}
