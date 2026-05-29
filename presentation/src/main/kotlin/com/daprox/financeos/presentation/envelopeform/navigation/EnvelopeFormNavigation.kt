package com.daprox.financeos.presentation.envelopeform.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.daprox.financeos.domain.model.EnvelopeTypeEnum
import com.daprox.financeos.presentation.envelopeform.EnvelopeFormScreenRoot
import kotlinx.serialization.Serializable

/**
 * Route for the envelope form screen.
 *   - envelopeId = null → create mode
 *   - envelopeId = non-null → edit mode for that envelope
 *   - presetType = serialized EnvelopeTypeEnum name, pre-selects the type in create mode
 */
@Serializable
data class EnvelopeForm(
    val envelopeId: String? = null,
    val presetType: String? = null,
)

fun NavGraphBuilder.envelopeFormScreen(onNavigateBack: () -> Unit = {}) {
    composable<EnvelopeForm> { backStackEntry ->
        val route = backStackEntry.toRoute<EnvelopeForm>()
        val presetType = route.presetType?.let {
            runCatching { EnvelopeTypeEnum.valueOf(it) }.getOrNull()
        }
        EnvelopeFormScreenRoot(
            envelopeId = route.envelopeId,
            presetType = presetType?.name,
            onNavigateBack = onNavigateBack,
        )
    }
}
