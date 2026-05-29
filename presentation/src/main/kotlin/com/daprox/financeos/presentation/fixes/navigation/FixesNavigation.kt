package com.daprox.financeos.presentation.fixes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.fixes.FixesScreenRoot
import kotlinx.serialization.Serializable

/**
 * Navigation route for the Fixes screen.
 *
 * A serializable object defining the route destination for the fixed envelope charges view.
 */
@Serializable
object Fixes

/**
 * Navigation extension to add the Fixes screen to the NavGraph.
 *
 * Usage:
 * ```kotlin
 * navGraphBuilder.fixesScreen(onNavigateBack = { navController.popBackStack() })
 * ```
 *
 * @param onNavigateBack Callback invoked when the user navigates back from the Fixes screen
 */
fun NavGraphBuilder.fixesScreen(onNavigateBack: () -> Unit = {}) {
    composable<Fixes> {
        FixesScreenRoot(onNavigateBack = onNavigateBack)
    }
}
