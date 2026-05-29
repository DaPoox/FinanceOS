package com.daprox.financeos.presentation.allocation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.allocation.AllocationScreenRoot
import kotlinx.serialization.Serializable

/**
 * Navigation route for the Allocation screen.
 *
 * A serializable object defining the route destination for the 3-step budget allocation wizard.
 */
@Serializable
object Allocation

/**
 * Navigation extension to add the Allocation screen to the NavGraph.
 *
 * Usage:
 * ```kotlin
 * navGraphBuilder.allocationScreen(onNavigateBack = { navController.popBackStack() })
 * ```
 *
 * @param onNavigateBack Callback invoked when the user navigates back from the Allocation screen
 */
fun NavGraphBuilder.allocationScreen(onNavigateBack: () -> Unit = {}) {
    composable<Allocation> {
        AllocationScreenRoot(onNavigateBack = onNavigateBack)
    }
}
