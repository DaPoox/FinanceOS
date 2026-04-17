package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Spacing token set. Based on a 4dp grid — the "Normal" spacing level from the
// design spec. Accessed via MaterialTheme.spacing in composables.

@Immutable
data class FinanceOSSpacing(
    val xs:   Dp = 4.dp,
    val sm:   Dp = 8.dp,
    val md:   Dp = 16.dp,
    val lg:   Dp = 24.dp,
    val xl:   Dp = 32.dp,
    val xxl:  Dp = 48.dp,
    val xxxl: Dp = 64.dp,
)

// CompositionLocal that carries the spacing instance down the tree.
// Provided by FinanceOSTheme; read via MaterialTheme.spacing.
internal val LocalSpacing = staticCompositionLocalOf { FinanceOSSpacing() }
