package com.daprox.financeos.presentation.envelopedetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.daprox.financeos.presentation.envelopedetail.EnvelopeDetailScreenRoot
import kotlinx.serialization.Serializable

/**
 * Navigation route for the Envelope Detail screen.
 *
 * A serializable data class defining the route destination with the envelope ID as a parameter.
 *
 * @param id The envelope ID to display details for
 */
@Serializable
data class EnvelopeDetail(val id: String)

/**
 * Navigation extension to add the Envelope Detail screen to the NavGraph.
 *
 * Usage:
 * ```kotlin
 * navGraphBuilder.envelopeDetailScreen(
 *     onNavigateBack = { navController.popBackStack() },
 *     onNavigateToEditEnvelope = { envelopeId -> navController.navigate(EnvelopeForm(envelopeId)) }
 * )
 * ```
 *
 * @param onNavigateBack Callback invoked when the user navigates back from the detail screen
 * @param onNavigateToEditEnvelope Callback invoked to navigate to the envelope edit screen; parameter is the envelope ID
 */
fun NavGraphBuilder.envelopeDetailScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToEditEnvelope: (String) -> Unit = {},
) {
    composable<EnvelopeDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<EnvelopeDetail>()
        EnvelopeDetailScreenRoot(
            id = route.id,
            onNavigateBack = onNavigateBack,
            onNavigateToEditEnvelope = onNavigateToEditEnvelope,
        )
    }
}
