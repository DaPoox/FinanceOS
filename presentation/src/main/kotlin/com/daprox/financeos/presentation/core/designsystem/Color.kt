package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// ── Raw palette ──────────────────────────────────────────────────────────────
// Surfaces — tonal stacking, no borders.
internal val Neutral1000 = Color(0xFF0A0D10)
internal val Neutral950  = Color(0xFF0F1417) // background
internal val Neutral900  = Color(0xFF171C1F) // surface-container-low (card boundary)
internal val Neutral850  = Color(0xFF1C2226)
internal val Neutral800  = Color(0xFF222930) // surface-container-high
internal val Neutral750  = Color(0xFF2B333B) // surface-container-highest (chips, pips)

// Text — off-white per the "no pure white" rule.
internal val Slate200 = Color(0xFFDFE3E7) // on-surface
internal val Slate400 = Color(0xFF8E9BA4)
internal val Slate500 = Color(0xFF64748B) // on-surface-variant (inactive nav, muted labels)
internal val Slate700 = Color(0xFF3A4650) // outline

// Primary — Green (Emerald jewel tone).
internal val Green400  = Color(0xFF6EE591) // primary
internal val Green950  = Color(0xFF003919) // on-primary
internal val Green900  = Color(0xFF005227) // primary-container
internal val Green200  = Color(0xFFA7F3D0) // on-primary-container

// Secondary — Blue (Sapphire).
internal val Blue300  = Color(0xFF93C5FD)
internal val Blue900  = Color(0xFF1E3A8A)
internal val Blue700  = Color(0xFF1D4ED8)
internal val Blue100  = Color(0xFFDBEAFE)

// Tertiary — Purple (Violet).
internal val Purple300 = Color(0xFFC4B5FD)
internal val Purple950 = Color(0xFF2E1065)
internal val Purple900 = Color(0xFF4C1D95)
internal val Purple100 = Color(0xFFEDE9FE)

// Error — Red, lightened for dark backgrounds.
internal val Red400 = Color(0xFFF87171)
internal val Red900 = Color(0xFF7F1D1D)
internal val Red800 = Color(0xFF991B1B)
internal val Red100 = Color(0xFFFEE2E2)

// ── Dark color scheme ────────────────────────────────────────────────────────
// The app is dark-first per the "Emerald Ledger" design system.
// Hierarchy is conveyed through tonal stacking — no borders, no shadows except
// the diffused primary glow on the FAB.

internal val FinanceOSDarkColorScheme = darkColorScheme(
    // Primary — Green
    primary            = Green400,
    onPrimary          = Green950,
    primaryContainer   = Green900,
    onPrimaryContainer = Green200,

    // Secondary — Blue
    secondary            = Blue300,
    onSecondary          = Blue900,
    secondaryContainer   = Blue700,
    onSecondaryContainer = Blue100,

    // Tertiary — Purple
    tertiary            = Purple300,
    onTertiary          = Purple950,
    tertiaryContainer   = Purple900,
    onTertiaryContainer = Purple100,

    // Surfaces — tonal stacking from Neutral950 base
    background              = Neutral950,
    onBackground            = Slate200,
    surface                 = Neutral950,
    onSurface               = Slate200,
    surfaceVariant          = Neutral850,
    onSurfaceVariant        = Slate500,
    surfaceContainerLowest  = Neutral1000,
    surfaceContainerLow     = Neutral900,
    surfaceContainer        = Neutral850,
    surfaceContainerHigh    = Neutral800,
    surfaceContainerHighest = Neutral750,
    surfaceTint             = Green400,

    // Outlines
    outline        = Slate700,
    outlineVariant = Neutral800,

    // Errors
    error            = Red400,
    onError          = Red900,
    errorContainer   = Red800,
    onErrorContainer = Red100,

    // Inverse
    scrim            = Color.Black,
    inverseSurface   = Slate200,
    inverseOnSurface = Neutral950,
    inversePrimary   = Green900,
)
