package com.daprox.financeos.presentation.budget.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.budget.BudgetScreenRoot
import kotlinx.serialization.Serializable

/** Route object for Budget navigation. */
@Serializable
object Budget

/**
 * Adds Budget screen to nav graph. Routes navigation events to callbacks.
 *
 * @param onNavigateToAllocation navigate to month allocation
 * @param onNavigateToEnvelopeDetail navigate to envelope detail with id
 * @param onNavigateToFixes navigate to fixed charges detail
 * @param onNavigateToAddEnvelope navigate to envelope form in create mode with preset type name
 */
fun NavGraphBuilder.budgetScreen(
    onNavigateToAllocation: () -> Unit = {},
    onNavigateToEnvelopeDetail: (String) -> Unit = {},
    onNavigateToFixes: () -> Unit = {},
    onNavigateToAddEnvelope: (String) -> Unit = {},
) {
    composable<Budget> {
        BudgetScreenRoot(
            onNavigateToAllocation = onNavigateToAllocation,
            onNavigateToEnvelopeDetail = onNavigateToEnvelopeDetail,
            onNavigateToFixes = onNavigateToFixes,
            onNavigateToAddEnvelope = onNavigateToAddEnvelope,
        )
    }
}
