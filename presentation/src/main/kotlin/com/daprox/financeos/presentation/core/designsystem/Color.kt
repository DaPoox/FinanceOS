package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * # Color System
 *
 * Raw palette definitions and semantic tokens for the FinanceOS design system.
 * The color system is split into two layers:
 *
 * 1. **Raw Palette** — Pure color values (Navy, Gold, etc.)
 * 2. **Semantic Tokens** — Named slots (Primary, Surface, OnSurface, etc.)
 *
 * Always use semantic tokens in components; the raw palette is implementation detail.
 */

// ─────────────────────────────────────────────
// RAW PALETTE — valeurs brutes, ne pas utiliser
// directement dans les composants.
// Toujours passer par les tokens sémantiques.
// ─────────────────────────────────────────────

/**
 * Raw color palette containing all base colors used in the design system.
 *
 * This object defines the atomic color values (Navy, Gold, text colors, semantic
 * colors) that feed into semantic token definitions. Do not use these colors
 * directly in Composables; always access them through semantic tokens
 * (Primary, Surface, Error, etc.) defined below.
 */
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

/** Semantic color token: screen background — darkest navy (Navy 950). */
internal val BackgroundDark = Palette.Navy950

/** Semantic color token: card and sheet surfaces — navy 900. */
internal val SurfaceDark = Palette.Navy900

/** Semantic color token: secondary cards and unselected chips — navy 800. */
internal val SurfaceVariantDark = Palette.Navy800

/** Semantic color token: input fields, bottom nav, bottom sheet backgrounds — navy 700. */
internal val SurfaceContainerDark = Palette.Navy700

/** Semantic color token: primary accent for CTAs, active nav items, and highlights — gold 400. */
internal val PrimaryDark = Palette.Gold400

/** Semantic color token: text and icons on primary background — gold 900. */
internal val OnPrimaryDark = Palette.Gold900

/** Semantic color token: primary text on surface backgrounds — white. */
internal val OnSurfaceDark = Palette.White

/** Semantic color token: secondary text, labels, and subtitles — slate light. */
internal val OnSurfaceVariantDark = Palette.SlateLight

/** Semantic color token: subtle borders and dividers — 6% white overlay. */
internal val OutlineDark = Palette.BorderSubtle

/** Semantic color token: more visible borders — 11% white overlay. */
internal val OutlineVariantDark = Palette.BorderDefault

/** Semantic color token: error states and budget overruns — red 400. */
internal val ErrorDark = Palette.Red400

/** Semantic color token: text on error backgrounds — white. */
internal val OnErrorDark = Palette.White

// ─────────────────────────────────────────────
// TOKENS CUSTOM — hors slots M3
// Pour les cas que Material Design ne couvre pas.
// Préfixés pour éviter la confusion avec M3.
// ─────────────────────────────────────────────

/**
 * Custom semantic token: positive indicator for wealth deltas and OK states — emerald 500.
 *
 * Use only to signal confirmed progression in net worth or positive changes.
 * Never use for generic text or neutral amounts.
 */
internal val FinPositive = Palette.Emerald500

/**
 * Custom semantic token: warning state for budget envelope utilization between 80–100% — orange 400.
 *
 * Warns the user that they are approaching or have nearly reached their envelope limit.
 */
internal val FinWarning = Palette.Orange400

/**
 * Custom semantic token: savings category color for charts, donuts, and category badges — blue 300.
 *
 * Consistently identifies savings allocation in visual breakdowns.
 */
internal val FinSavings = Palette.Blue300

/**
 * Custom semantic token: investment category color for charts and category badges — violet 400.
 *
 * Consistently identifies investment allocation in visual breakdowns.
 */
internal val FinInvestment = Palette.Violet400

/**
 * Custom semantic token: neutral bar color for market contribution in wealth charts — slate neutral.
 *
 * Represents the portion of wealth gain attributable to market movement.
 */
internal val FinMarket = Palette.SlateNeutral

/**
 * Custom semantic token: semi-transparent overlay for bottom sheet scrim — 80% opacity navy 950.
 *
 * Dims the background when a modal or bottom sheet is presented.
 */
internal val ScrimDark = Color(0xCC090C12)  // 80% Navy950
