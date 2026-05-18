package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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

@Immutable
data class FinanceColors(
    // Indicateur positif — delta patrimonial, statut OK
    val positive: Color,
    // Warning — enveloppe >80% du budget
    val warning: Color,
    // Épargne — charts, donuts, barres dédiées
    val savings: Color,
    // Investissement — charts, donuts
    val investment: Color,
    // Marché — barre neutre contribution marché dans les charts
    val market: Color,
)

val LocalFinanceColors = staticCompositionLocalOf {
    FinanceColors(
        positive = FinPositive,
        warning = FinWarning,
        savings = FinSavings,
        investment = FinInvestment,
        market = FinMarket,
    )
}

// Extension pour accéder aux tokens custom proprement
val MaterialTheme.finColors: FinanceColors
    @Composable get() = LocalFinanceColors.current

// ─────────────────────────────────────────────
// FINANCE OS THEME — point d'entrée unique
// Wraps MaterialTheme + injecte les tokens custom
// ─────────────────────────────────────────────

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

val MaterialTheme.spacing: FinanceOSSpacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
