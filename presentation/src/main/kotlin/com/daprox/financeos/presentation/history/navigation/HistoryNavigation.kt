package com.daprox.financeos.presentation.history.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.daprox.financeos.presentation.history.HistoryScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object History

fun NavGraphBuilder.historyScreen() {
    composable<History> {
        HistoryScreenRoot()
    }
}
