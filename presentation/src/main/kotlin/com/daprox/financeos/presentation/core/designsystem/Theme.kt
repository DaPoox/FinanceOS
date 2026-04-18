package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

// Root theme composable. All screens and previews must be wrapped in FinanceOSTheme.
// The app is dark-first per the "Emerald Ledger" design system.
@Composable
fun FinanceOSTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpacing provides FinanceOSSpacing()) {
        MaterialTheme(
            colorScheme = FinanceOSDarkColorScheme,
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
