package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.R

/**
 * # Typography System
 *
 * Two-font system for the FinanceOS design:
 *
 * - **DM Sans:** For UI text (titles, labels, buttons, descriptions)
 * - **Geist Mono:** For financial numbers (amounts, percentages, dates)
 *
 * ## Usage
 *
 * DM Sans is applied to Material Design 3's title, body, and label styles.
 * Geist Mono is applied to display and headline styles (reserved for numbers).
 *
 * ```kotlin
 * Text(text = "4 200 €", style = MaterialTheme.typography.headlineLarge)
 * // ↑ headlineLarge uses Geist Mono (defined below)
 * ```
 */

/**
 * DM Sans font family with weights: Regular, Medium, SemiBold, Bold.
 *
 * Use for all UI text: titles, labels, buttons, descriptions, and body copy.
 */
internal val DmSans = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium, FontWeight.Medium),
    Font(R.font.dm_sans_semibold, FontWeight.SemiBold),
    Font(R.font.dm_sans_bold, FontWeight.Bold),
)

/**
 * Geist Mono font family with weights: Regular, Medium, SemiBold, Bold.
 *
 * Use exclusively for financial numbers: amounts, percentages, dates, and numeric labels.
 */
internal val GeistMono = FontFamily(
    Font(R.font.geist_mono_regular, FontWeight.Normal),
    Font(R.font.geist_mono_medium, FontWeight.Medium),
    Font(R.font.geist_mono_semibold, FontWeight.SemiBold),
    Font(R.font.geist_mono_bold, FontWeight.Bold),
)

/**
 * Material Design 3 typography scale for FinanceOS.
 *
 * The scale is split into two font rules:
 * - **Headline styles** (Display, Headline) use Geist Mono for financial numbers
 * - **Title, Body, Label styles** use DM Sans for UI text
 *
 * Access these styles via MaterialTheme.typography in composables.
 *
 * Example:
 * ```kotlin
 * Text(text = "Income", style = MaterialTheme.typography.titleLarge)
 * Text(text = "€5,000", style = MaterialTheme.typography.headlineMedium)
 * ```
 */
internal val FinanceOSTypography = Typography(
    // ── DISPLAY — montant hero (net worth, revenu du mois)
    /** 40sp Bold Geist Mono — hero financial amount (net worth, monthly income). */
    displayLarge = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp,
    ),

    // ── HEADLINE — montants secondaires importants
    /** 28sp Bold Geist Mono — secondary financial amounts (envelope balance, savings). */
    headlineLarge = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.3).sp,
    ),
    /** 22sp SemiBold Geist Mono — tertiary financial amounts (transaction values). */
    headlineMedium = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
    ),
    /** 18sp SemiBold Geist Mono — small financial numbers and percentages. */
    headlineSmall = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),

    // ── TITLE — titres d'écrans et de sections (UI, pas chiffres)
    /** 20sp Bold DM Sans — primary screen and section titles. */
    titleLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp,
    ),
    /** 16sp SemiBold DM Sans — secondary titles and subsection headers. */
    titleMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
    ),
    /** 14sp SemiBold DM Sans — small titles and prominent labels. */
    titleSmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),

    // ── BODY — texte courant, descriptions, notes
    /** 16sp Normal DM Sans — primary body text and descriptions. */
    bodyLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    /** 14sp Normal DM Sans — standard body copy and descriptions. */
    bodyMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    /** 12sp Normal DM Sans — small body text and captions. */
    bodySmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),

    // ── LABEL — labels de nav, chips, tags, section headers
    /** 13sp SemiBold DM Sans — navigation labels and prominent chips. */
    labelLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
    ),
    /** 11sp Medium DM Sans — medium labels and tag text. */
    labelMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.08.sp,
    ),
    /** 10sp Medium DM Sans — small labels, badges, and fine print. */
    labelSmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.1.sp,
    ),
)
