package com.daprox.financeos.presentation.accountform.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.daprox.financeos.presentation.accountform.AccountFormScreenRoot
import kotlinx.serialization.Serializable

/**
 * Route configuration for the account form screen.
 *
 * Supports both create and edit modes via the [accountId] parameter.
 *
 * @property accountId null = create mode; non-null = edit mode for that account
 */
@Serializable
data class AccountForm(val accountId: String? = null)

/**
 * Registers the account form screen in the navigation graph.
 *
 * Parses route arguments and sets up navigation callbacks.
 *
 * @param onNavigateBack callback on successful save, delete, or back action
 */
fun NavGraphBuilder.accountFormScreen(onNavigateBack: () -> Unit = {}) {
    composable<AccountForm> { backStackEntry ->
        val route = backStackEntry.toRoute<AccountForm>()
        AccountFormScreenRoot(
            accountId = route.accountId,
            onNavigateBack = onNavigateBack,
        )
    }
}
