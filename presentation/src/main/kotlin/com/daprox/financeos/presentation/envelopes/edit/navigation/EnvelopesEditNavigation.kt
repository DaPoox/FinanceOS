package com.daprox.financeos.presentation.envelopes.edit.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.envelopes.edit.EnvelopesEditScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object EnvelopesEdit

fun NavGraphBuilder.envelopesEditScreen(
    // ✕ — discard changes and pop back.
    onClose   : () -> Unit,
    // ✓ — persist changes (ViewModel handles the save) and pop back.
    onConfirm : () -> Unit,
) {
    composable<EnvelopesEdit> {
        EnvelopesEditScreenRoot(
            onClose   = onClose,
            onConfirm = onConfirm,
        )
    }
}
