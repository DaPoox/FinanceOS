package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * # Spacing System
 *
 * Spacing token set based on a 4dp grid — the "Normal" spacing level from the design spec.
 *
 * Access these tokens via `MaterialTheme.spacing` in composables when using FinanceOSTheme.
 *
 * Example:
 * ```kotlin
 * Box(modifier = Modifier.padding(MaterialTheme.spacing.md))
 * ```
 *
 * @property xs    4dp — tight spacing between small elements.
 * @property sm    8dp — normal spacing within components.
 * @property md    16dp — standard vertical/horizontal spacing between sections.
 * @property lg    24dp — prominent spacing between major sections.
 * @property xl    32dp — large vertical spacing for section breaks.
 * @property xxl   48dp — extra-large spacing for screen-level separation.
 * @property xxxl  64dp — maximum spacing for major layout separation.
 */
@Immutable
data class FinanceOSSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
    val xxxl: Dp = 64.dp,
)

/**
 * CompositionLocal that provides the spacing token instance down the tree.
 *
 * Instantiated by FinanceOSTheme and accessed via MaterialTheme.spacing extension.
 *
 * @see FinanceOSTheme
 */
internal val LocalSpacing = staticCompositionLocalOf { FinanceOSSpacing() }
