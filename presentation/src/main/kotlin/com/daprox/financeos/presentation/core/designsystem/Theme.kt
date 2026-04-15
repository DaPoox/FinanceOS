package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

// Root theme composable. All screens and previews must be wrapped in FinanceOSTheme.
// The app targets light mode only — darkTheme is kept for future flexibility.
@Composable
fun FinanceOSTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSpacing provides FinanceOSSpacing()) {
        MaterialTheme(
            colorScheme = if (darkTheme) FinanceOSLightColorScheme else FinanceOSLightColorScheme,
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
