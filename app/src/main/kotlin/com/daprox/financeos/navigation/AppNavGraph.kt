package com.daprox.financeos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

// Root nav graph. Assembles all feature graphs and wires cross-feature navigation via lambdas.
// Feature graphs are added here as NavGraphBuilder extensions from :presentation.
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "placeholder" // Replace with first feature route
    ) {
        // Feature graphs are registered here as they are built.
        // Example:
        // budgetGraph(
        //     navController = navController,
        //     onNavigateToDashboard = { navController.navigate(DashboardRoute) }
        // )
    }
}
