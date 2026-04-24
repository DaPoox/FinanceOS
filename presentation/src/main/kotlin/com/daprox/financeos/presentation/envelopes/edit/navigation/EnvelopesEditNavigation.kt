package com.daprox.financeos.presentation.envelopes.edit.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.envelopes.edit.EnvelopesEditScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object EnvelopesEdit

fun NavGraphBuilder.envelopesEditScreen(
    onNavigateBack: () -> Unit,
) {
    composable<EnvelopesEdit> {
        EnvelopesEditScreenRoot(onNavigateBack = onNavigateBack)
    }
}
