package com.daprox.financeos.presentation.budget.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.budget.BudgetScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object Budget

fun NavGraphBuilder.budgetScreen(
    onNavigateToAllocation: () -> Unit = {},
    onNavigateToEnvelopeDetail: (String) -> Unit = {},
    onNavigateToFixes: () -> Unit = {},
) {
    composable<Budget> {
        BudgetScreenRoot(
            onNavigateToAllocation = onNavigateToAllocation,
            onNavigateToEnvelopeDetail = onNavigateToEnvelopeDetail,
            onNavigateToFixes = onNavigateToFixes,
        )
    }
}
