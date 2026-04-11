package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme()
private val DarkColorScheme = darkColorScheme()

// Root theme composable. All screens and previews must be wrapped in FinanceOSTheme.
@Composable
fun FinanceOSTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = FinanceOSTypography,
        content = content
    )
}
