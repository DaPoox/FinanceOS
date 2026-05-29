package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * # Theme System
 *
 * Centralized theme provider for the FinanceOS design system.
 *
 * Combines Material Design 3 (MD3) color scheme with custom semantic tokens
 * (colors, spacing, shapes) into a single FinanceOSTheme Composable.
 *
 * All screens should be wrapped with FinanceOSTheme at the app level to ensure
 * consistent styling across the entire application.
 */

// ─────────────────────────────────────────────
// M3 COLOR SCHEME
// On alimente les slots M3 avec notre palette.
// Les composants Compose (Button, Card, TextField)
// lisent ces slots automatiquement.
// ─────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = SurfaceVariantDark,   // container du primary = surface variante
    onPrimaryContainer = OnSurfaceDark,

    background = BackgroundDark,
    onBackground = OnSurfaceDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceContainer = SurfaceContainerDark,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    error = ErrorDark,
    onError = OnErrorDark,

    scrim = ScrimDark,
)

// ─────────────────────────────────────────────
// TOKENS CUSTOM — extension de MaterialTheme
// Accessibles via MaterialTheme.finColors
// Exemple : MaterialTheme.finColors.positive
// ─────────────────────────────────────────────

/**
 * Custom finance-domain semantic color tokens not covered by Material Design 3.
 *
 * These tokens are accessed via `MaterialTheme.finColors` in composables and provide
 * consistent coloring for finance-specific categories and states.
 *
 * @property positive   Color for positive wealth deltas and OK states.
 * @property warning    Color for budget envelopes approaching their limit (80–100%).
 * @property savings    Color for savings category in charts and breakdowns.
 * @property investment Color for investment category in charts and breakdowns.
 * @property market     Neutral color for market contribution in wealth charts.
 *
 * @see MaterialTheme.finColors to access these tokens.
 */
@Immutable
data class FinanceColors(
    val positive: Color,
    val warning: Color,
    val savings: Color,
    val investment: Color,
    val market: Color,
)

/**
 * CompositionLocal that provides the FinanceColors token instance down the tree.
 *
 * Instantiated by FinanceOSTheme and accessed via MaterialTheme.finColors extension.
 *
 * @see FinanceOSTheme
 */
val LocalFinanceColors = staticCompositionLocalOf {
    FinanceColors(
        positive = FinPositive,
        warning = FinWarning,
        savings = FinSavings,
        investment = FinInvestment,
        market = FinMarket,
    )
}

/**
 * Extension property to access custom finance color tokens from MaterialTheme.
 *
 * Use this to access domain-specific colors for budget and wealth visualization.
 *
 * Example:
 * ```kotlin
 * Text(
 *   text = "+$1,200",
 *   color = MaterialTheme.finColors.positive
 * )
 * ```
 *
 * @return The current FinanceColors instance from the composition.
 */
val MaterialTheme.finColors: FinanceColors
    @Composable get() = LocalFinanceColors.current

// ─────────────────────────────────────────────
// FINANCE OS THEME — point d'entrée unique
// Wraps MaterialTheme + injecte les tokens custom
// ─────────────────────────────────────────────

/**
 * Root theme Composable for the FinanceOS application.
 *
 * Wraps Material Design 3 MaterialTheme and injects custom semantic tokens
 * (spacing, finance colors) via CompositionLocal providers. All app screens
 * should be wrapped with this theme to ensure consistent styling.
 *
 * Combines:
 * - DarkColorScheme (Material Design 3 color slots)
 * - FinanceOSTypography (DM Sans + Geist Mono fonts)
 * - FinanceOSShapes (rounded corner scale)
 * - FinanceOSSpacing (spacing token scale)
 * - FinanceColors (custom domain-specific color tokens)
 *
 * @param content The app content to theme — typically the root Composable
 *                or navigation graph.
 *
 * Example:
 * ```kotlin
 * FinanceOSTheme {
 *   AppNavGraph()
 * }
 * ```
 *
 * @see FinanceOSSpacing
 * @see FinanceColors
 * @see FinanceOSTypography
 */
@Composable
fun FinanceOSTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalSpacing provides FinanceOSSpacing(),
        LocalFinanceColors provides FinanceColors(
            positive = FinPositive,
            warning = FinWarning,
            savings = FinSavings,
            investment = FinInvestment,
            market = FinMarket,
        ),
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = FinanceOSTypography,
            shapes = FinanceOSShapes,
            content = content,
        )
    }
}

/**
 * Extension property to access spacing tokens from MaterialTheme.
 *
 * Use this to access the spacing scale in any composable wrapped by FinanceOSTheme.
 *
 * Example:
 * ```kotlin
 * Box(modifier = Modifier.padding(MaterialTheme.spacing.md))
 * ```
 *
 * @return The current FinanceOSSpacing instance from the composition.
 */
val MaterialTheme.spacing: FinanceOSSpacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
