package com.daprox.financeos.presentation.envelopedetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.daprox.financeos.presentation.envelopedetail.EnvelopeDetailScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data class EnvelopeDetail(val id: String)

fun NavGraphBuilder.envelopeDetailScreen(onNavigateBack: () -> Unit = {}) {
    composable<EnvelopeDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<EnvelopeDetail>()
        EnvelopeDetailScreenRoot(
            id = route.id,
            onNavigateBack = onNavigateBack,
        )
    }
}
