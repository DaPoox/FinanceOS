package com.daprox.financeos.presentation.history.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.history.HistoryScreenRoot
import kotlinx.serialization.Serializable

/**
 * Route configuration for the history (12-month summary) screen.
 *
 * Simple object route with no parameters.
 */
@Serializable
object History

/**
 * Registers the history screen in the navigation graph.
 *
 * Simple registration with no navigation callbacks (history screen is read-only).
 */
fun NavGraphBuilder.historyScreen() {
    composable<History> {
        HistoryScreenRoot()
    }
}
