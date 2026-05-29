package com.daprox.financeos.presentation.patrimoine.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.patrimoine.PatrimoineScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object Patrimoine

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
