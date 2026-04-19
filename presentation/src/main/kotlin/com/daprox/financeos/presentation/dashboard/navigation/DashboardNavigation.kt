package com.daprox.financeos.presentation.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.dashboard.DashboardScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object Dashboard

fun NavGraphBuilder.dashboardScreen() {
    composable<Dashboard> {
        DashboardScreenRoot()
    }
}
