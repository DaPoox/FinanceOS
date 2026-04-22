package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

// CompositionLocal for custom category (domain) colors — not part of M3's role system.
// Defaults to dark theme so previews outside FinanceOSTheme still compile.
val LocalCategoryColors = staticCompositionLocalOf { FinanceOSDarkCategoryColors }

// Root theme composable. All screens and previews must be wrapped in FinanceOSTheme.
// The app is dark-first per the "Emerald Ledger" design system.
@Composable
fun FinanceOSTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme    = if (darkTheme) FinanceOSDarkColorScheme  else FinanceOSDarkColorScheme
    val categoryColors = if (darkTheme) FinanceOSDarkCategoryColors else FinanceOSLightCategoryColors

    CompositionLocalProvider(
        LocalSpacing provides FinanceOSSpacing(),
        LocalCategoryColors provides categoryColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = FinanceOSTypography,
            shapes      = FinanceOSShapes,
            content     = content,
        )
    }
}

// Extension to access spacing tokens via MaterialTheme — consistent with how
// M3 exposes colorScheme, typography, and shapes.
val MaterialTheme.spacing: FinanceOSSpacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

// Extension to access custom domain category colors — same pattern as spacing.
val MaterialTheme.categoryColors: FinanceOSCategoryColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCategoryColors.current
