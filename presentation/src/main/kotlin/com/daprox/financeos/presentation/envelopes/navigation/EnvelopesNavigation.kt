package com.daprox.financeos.presentation.envelopes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.envelopes.EnvelopesScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object Envelopes

fun NavGraphBuilder.envelopesScreen(
    onNavigateToEdit: () -> Unit = {},
) {
    composable<Envelopes> {
        EnvelopesScreenRoot(onNavigateToEdit = onNavigateToEdit)
    }
}
