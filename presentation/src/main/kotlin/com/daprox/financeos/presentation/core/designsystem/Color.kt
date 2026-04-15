package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Raw palette ──────────────────────────────────────────────────────────────
// Named by hue and shade. Reference these internally; never expose raw values
// to feature composables — they should use MaterialTheme.colorScheme roles.

internal val Emerald600 = Color(0xFF059669)
internal val Emerald100 = Color(0xFFD1FAE5)
internal val Emerald900 = Color(0xFF064E3B)

internal val Blue600 = Color(0xFF2563EB)
internal val Blue100 = Color(0xFFDBEAFE)
internal val Blue900 = Color(0xFF1E3A8A)

internal val Violet500 = Color(0xFF8B5CF6)
internal val Violet100 = Color(0xFFEDE9FE)
internal val Violet900 = Color(0xFF4C1D95)

internal val Slate900 = Color(0xFF0F172A)
internal val Slate700 = Color(0xFF334155)
internal val Slate400 = Color(0xFF94A3B8)
internal val Slate200 = Color(0xFFE2E8F0)
internal val Slate100 = Color(0xFFF1F5F9)
internal val Slate50  = Color(0xFFF8FAFC)

internal val White = Color(0xFFFFFFFF)

internal val Red600  = Color(0xFFDC2626)
internal val Red100  = Color(0xFFFEE2E2)
internal val Red900  = Color(0xFF7F1D1D)

// ── Semantic color scheme (light only) ───────────────────────────────────────
// The app targets light mode only per the design spec. Mapped to Material3 roles
// so all M3 components pick up brand colors automatically.

internal val FinanceOSLightColorScheme = lightColorScheme(
    // Primary — Emerald (brand green, CTAs, key interactive elements)
    primary            = Emerald600,
    onPrimary          = White,
    primaryContainer   = Emerald100,
    onPrimaryContainer = Emerald900,

    // Secondary — Blue (secondary actions, info hierarchy)
    secondary            = Blue600,
    onSecondary          = White,
    secondaryContainer   = Blue100,
    onSecondaryContainer = Blue900,

    // Tertiary — Violet (highlights, badges, decorative elements)
    tertiary            = Violet500,
    onTertiary          = White,
    tertiaryContainer   = Violet100,
    onTertiaryContainer = Violet900,

    // Backgrounds & surfaces
    background        = White,
    onBackground      = Slate900,
    surface           = White,
    onSurface         = Slate900,
    surfaceVariant    = Slate100,
    onSurfaceVariant  = Slate700,
    surfaceTint       = Emerald600,

    // Outlines
    outline        = Slate200,
    outlineVariant = Slate100,

    // Errors
    error            = Red600,
    onError          = White,
    errorContainer   = Red100,
    onErrorContainer = Red900,

    // Inverse / scrim
    scrim            = Slate900,
    inverseSurface   = Slate900,
    inverseOnSurface = Slate50,
    inversePrimary   = Emerald100,
)
