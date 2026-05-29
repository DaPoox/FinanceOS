package com.daprox.financeos.presentation.accountform.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.daprox.financeos.presentation.accountform.AccountFormScreenRoot
import kotlinx.serialization.Serializable

/** Route for create mode (accountId = null) and edit mode (accountId = non-null id). */
@Serializable
data class AccountForm(val accountId: String? = null)

fun NavGraphBuilder.accountFormScreen(onNavigateBack: () -> Unit = {}) {
    composable<AccountForm> { backStackEntry ->
        val route = backStackEntry.toRoute<AccountForm>()
        AccountFormScreenRoot(
            accountId = route.accountId,
            onNavigateBack = onNavigateBack,
        )
    }
}
