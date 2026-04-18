package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// ── Raw palette ──────────────────────────────────────────────────────────────
// Obsidian base — surfaces built through tonal stacking, no borders.

internal val Obsidian           = Color(0xFF0F1417) // background
internal val ObsidianLowest     = Color(0xFF0A0D10)
internal val ObsidianLow        = Color(0xFF171C1F) // surface-container-low (card boundary)
internal val ObsidianContainer  = Color(0xFF1C2226)
internal val ObsidianHigh       = Color(0xFF222930) // surface-container-high
internal val ObsidianHighest    = Color(0xFF2B333B) // surface-container-highest (chips, pips)

// Text — off-white per the "no pure white" rule.
internal val Mist               = Color(0xFFDFE3E7) // on-surface
internal val MistDim            = Color(0xFF8E9BA4) // on-surface-variant
internal val MistFaint          = Color(0xFF3A4650) // outline

// Primary — Emerald, vibrant jewel tone on dark.
internal val Emerald400         = Color(0xFF6EE591) // primary (tone ~80 for dark)
internal val EmeraldDark        = Color(0xFF003919) // on-primary
internal val EmeraldContainer   = Color(0xFF005227) // primary-container
internal val EmeraldLight       = Color(0xFFA7F3D0) // on-primary-container

// Secondary — Sapphire.
internal val Sapphire300        = Color(0xFF93C5FD)
internal val SapphireDark       = Color(0xFF1E3A8A)
internal val SapphireContainer  = Color(0xFF1D4ED8)
internal val SapphireLight      = Color(0xFFDBEAFE)

// Tertiary — Violet.
internal val Violet300          = Color(0xFFC4B5FD)
internal val VioletDark         = Color(0xFF2E1065)
internal val VioletContainer    = Color(0xFF4C1D95)
internal val VioletLight        = Color(0xFFEDE9FE)

// Error — red, lightened for dark backgrounds.
internal val Red400             = Color(0xFFF87171)
internal val RedDark            = Color(0xFF7F1D1D)
internal val RedContainer       = Color(0xFF991B1B)
internal val RedLight           = Color(0xFFFEE2E2)

// ── Dark color scheme ────────────────────────────────────────────────────────
// The app is dark-first per the "Emerald Ledger" design system.
// Hierarchy is conveyed through tonal stacking — no borders, no shadows except
// the diffused primary glow on the FAB.

internal val FinanceOSDarkColorScheme = darkColorScheme(
    // Primary — Emerald
    primary            = Emerald400,
    onPrimary          = EmeraldDark,
    primaryContainer   = EmeraldContainer,
    onPrimaryContainer = EmeraldLight,

    // Secondary — Sapphire
    secondary            = Sapphire300,
    onSecondary          = SapphireDark,
    secondaryContainer   = SapphireContainer,
    onSecondaryContainer = SapphireLight,

    // Tertiary — Violet
    tertiary            = Violet300,
    onTertiary          = VioletDark,
    tertiaryContainer   = VioletContainer,
    onTertiaryContainer = VioletLight,

    // Surfaces — tonal stacking from the Obsidian base
    background           = Obsidian,
    onBackground         = Mist,
    surface              = Obsidian,
    onSurface            = Mist,
    surfaceVariant       = ObsidianContainer,
    onSurfaceVariant     = MistDim,
    surfaceContainerLowest  = ObsidianLowest,
    surfaceContainerLow     = ObsidianLow,
    surfaceContainer        = ObsidianContainer,
    surfaceContainerHigh    = ObsidianHigh,
    surfaceContainerHighest = ObsidianHighest,
    surfaceTint          = Emerald400,

    // Outlines — subtle, for the rare cases a boundary is needed
    outline        = MistFaint,
    outlineVariant = ObsidianHigh,

    // Errors
    error            = Red400,
    onError          = RedDark,
    errorContainer   = RedContainer,
    onErrorContainer = RedLight,

    // Inverse
    scrim            = Color.Black,
    inverseSurface   = Mist,
    inverseOnSurface = Obsidian,
    inversePrimary   = EmeraldContainer,
)
