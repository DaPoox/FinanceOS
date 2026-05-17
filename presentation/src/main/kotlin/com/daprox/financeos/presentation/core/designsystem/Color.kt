package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
// RAW PALETTE — valeurs brutes, ne pas utiliser
// directement dans les composants.
// Toujours passer par les tokens sémantiques.
// ─────────────────────────────────────────────

internal object Palette {
    // Navy
    val Navy950 = Color(0xFF090C12)
    val Navy900 = Color(0xFF0F1420)
    val Navy800 = Color(0xFF161D2E)
    val Navy700 = Color(0xFF1C263A)
    val Navy600 = Color(0xFF243347)

    // Gold
    val Gold400 = Color(0xFFF0B429)
    val Gold900 = Color(0xFF1A1000)

    // Text
    val White = Color(0xFFE8EEF5)
    val SlateLight = Color(0xFF8BA4BE)
    val SlateDark = Color(0xFF4A6070)

    // Semantic
    val Emerald500 = Color(0xFF22C55E)
    val Red400 = Color(0xFFF87171)
    val Orange400 = Color(0xFFFB923C)
    val Blue300 = Color(0xFF7EB8F7)
    val Violet400 = Color(0xFFA78BFA)

    // Borders
    val BorderSubtle = Color(0x0FFFFFFF)   // 6% white
    val BorderDefault = Color(0x1CFFFFFF)  // 11% white

    // Chart neutral — market contribution bar
    val SlateNeutral = Color(0xFF4A5568)
}

// ─────────────────────────────────────────────
// SLOTS M3 — ColorScheme
// Nommés selon Material Design 3.
// Ces valeurs alimentent MaterialTheme.colorScheme.
// ─────────────────────────────────────────────

// Background — fond de chaque écran
internal val BackgroundDark = Palette.Navy950

// Surface — fond des cards et sheets
internal val SurfaceDark = Palette.Navy900

// SurfaceVariant — cards secondaires, chips non sélectionnées
internal val SurfaceVariantDark = Palette.Navy800

// SurfaceContainer — inputs, bottom nav, bottom sheet
internal val SurfaceContainerDark = Palette.Navy700

// Primary — CTA, nav active, accent principal
internal val PrimaryDark = Palette.Gold400

// OnPrimary — texte/icône sur fond primary
internal val OnPrimaryDark = Palette.Gold900

// OnSurface — texte principal sur fond surface
internal val OnSurfaceDark = Palette.White

// OnSurfaceVariant — texte secondaire, labels, sous-titres
internal val OnSurfaceVariantDark = Palette.SlateLight

// Outline — bordures subtiles
internal val OutlineDark = Palette.BorderSubtle

// OutlineVariant — bordures légèrement plus visibles
internal val OutlineVariantDark = Palette.BorderDefault

// Error — dépassement de budget, états d'erreur
internal val ErrorDark = Palette.Red400

// OnError — texte sur fond error
internal val OnErrorDark = Palette.White

// ─────────────────────────────────────────────
// TOKENS CUSTOM — hors slots M3
// Pour les cas que Material Design ne couvre pas.
// Préfixés pour éviter la confusion avec M3.
// ─────────────────────────────────────────────

// Indicateur positif — deltas patrimoniaux, état OK
// RÈGLE : jamais pour du texte générique ou des montants neutres.
// Uniquement pour signaler une progression confirmée.
internal val FinPositive = Palette.Emerald500

// Warning — enveloppe entre 80% et 100% du budget
internal val FinWarning = Palette.Orange400

// Savings — couleur dédiée épargne dans les charts et donuts
internal val FinSavings = Palette.Blue300

// Investment — couleur dédiée investissement dans les charts
internal val FinInvestment = Palette.Violet400

// Market — barre neutre pour la contribution marché dans les charts
internal val FinMarket = Palette.SlateNeutral

// Scrim — overlay semi-transparent pour les bottom sheets
internal val ScrimDark = Color(0xCC090C12)  // 80% Navy950
