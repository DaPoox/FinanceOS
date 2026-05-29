package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * # Shape System
 *
 * Rounded corner scale for the FinanceOS design system.
 *
 * The design spec calls for maximum roundedness, with the scale leaning toward
 * pill-shaped (fully rounded) corners at larger sizes. ExtraSmall is the only
 * "conservative" step; every larger increment pushes further toward pill territory.
 *
 * These shapes are fed into MaterialTheme and automatically applied to standard
 * Compose components (Button, Card, TextField) when using FinanceOSTheme.
 */
internal val FinanceOSShapes = Shapes(
    /** 8dp — conservative roundedness for chips, tooltips, and small badges. */
    extraSmall = RoundedCornerShape(8.dp),
    /** 12dp — input fields, small cards, and tight form elements. */
    small = RoundedCornerShape(12.dp),
    /** 16dp — standard cards, dialogs, and bottom sheets. */
    medium = RoundedCornerShape(16.dp),
    /** 24dp — prominent modals, drawers, and large surfaces. */
    large = RoundedCornerShape(24.dp),
    /** 32dp — full-bleed containers, FABs, and ultra-rounded surfaces (pill-shaped). */
    extraLarge = RoundedCornerShape(32.dp),
)
