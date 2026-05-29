package com.daprox.financeos.presentation.envelopeform.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.daprox.financeos.domain.model.EnvelopeTypeEnum
import com.daprox.financeos.presentation.envelopeform.EnvelopeFormScreenRoot
import kotlinx.serialization.Serializable

/**
 * Route configuration for the envelope form screen.
 *
 * Supports both create and edit modes via the [envelopeId] parameter. The [presetType] allows
 * pre-selection of envelope type (e.g. when creating a new SAVINGS envelope from the dashboard).
 *
 * @property envelopeId null = create mode; non-null = edit mode for that envelope
 * @property presetType serialized EnvelopeTypeEnum name; pre-selected in create mode (e.g. "SAVINGS")
 */
@Serializable
data class EnvelopeForm(
    val envelopeId: String? = null,
    val presetType: String? = null,
)

/**
 * Registers the envelope form screen in the navigation graph.
 *
 * Parses route arguments, validates presetType, and sets up navigation callbacks.
 *
 * @param onNavigateBack callback on successful save, delete, or back action
 */
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
