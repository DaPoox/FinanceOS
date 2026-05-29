package com.daprox.financeos.presentation.patrimoine.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.patrimoine.PatrimoineScreenRoot
import kotlinx.serialization.Serializable

/**
 * Route configuration for the patrimoine (net worth) screen.
 *
 * Simple object route with no parameters. Navigation to account create/edit is handled via
 * events from [PatrimoineViewModel].
 */
@Serializable
object Patrimoine

/**
 * Registers the patrimoine screen in the navigation graph.
 *
 * Sets up navigation callbacks for account management.
 *
 * @param onNavigateToAddAccount callback to navigate to account create screen
 * @param onNavigateToEditAccount callback to navigate to account edit screen with given id
 */
fun NavGraphBuilder.patrimoineScreen(
    onNavigateToAddAccount: () -> Unit = {},
    onNavigateToEditAccount: (String) -> Unit = {},
) {
    composable<Patrimoine> {
        PatrimoineScreenRoot(
            onNavigateToAddAccount = onNavigateToAddAccount,
            onNavigateToEditAccount = onNavigateToEditAccount,
        )
    }
}
