package com.daprox.financeos.presentation.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.dashboard.DashboardScreenRoot
import kotlinx.serialization.Serializable

/** Route object for Dashboard navigation. */
@Serializable
object Dashboard

/**
 * Adds Dashboard screen to nav graph. Routes navigation events to callbacks.
 *
 * @param onNavigateToBudget navigate to budget/envelope list
 * @param onNavigateToAllocation navigate to month allocation
 * @param onNavigateToEnvelopeDetail navigate to envelope detail with id
 * @param onNavigateToMonthHistory navigate to month history with id
 */
fun NavGraphBuilder.dashboardScreen(
    onNavigateToBudget: () -> Unit = {},
    onNavigateToAllocation: () -> Unit = {},
    onNavigateToEnvelopeDetail: (String) -> Unit = {},
    onNavigateToMonthHistory: (String) -> Unit = {},
) {
    composable<Dashboard> {
        DashboardScreenRoot(
            onNavigateToBudget = onNavigateToBudget,
            onNavigateToAllocation = onNavigateToAllocation,
            onNavigateToEnvelopeDetail = onNavigateToEnvelopeDetail,
            onNavigateToMonthHistory = onNavigateToMonthHistory,
        )
    }
}
