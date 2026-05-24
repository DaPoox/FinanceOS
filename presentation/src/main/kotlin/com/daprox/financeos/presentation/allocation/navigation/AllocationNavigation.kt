package com.daprox.financeos.presentation.allocation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.allocation.AllocationScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object Allocation

fun NavGraphBuilder.allocationScreen(onNavigateBack: () -> Unit = {}) {
    composable<Allocation> {
        AllocationScreenRoot(onNavigateBack = onNavigateBack)
    }
}
