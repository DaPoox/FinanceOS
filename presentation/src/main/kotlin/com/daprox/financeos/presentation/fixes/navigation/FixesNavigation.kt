package com.daprox.financeos.presentation.fixes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.fixes.FixesScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object Fixes

fun NavGraphBuilder.fixesScreen(onNavigateBack: () -> Unit = {}) {
    composable<Fixes> {
        FixesScreenRoot(onNavigateBack = onNavigateBack)
    }
}
