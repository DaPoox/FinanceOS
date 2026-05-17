package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.R

// ─────────────────────────────────────────────
// FONT FAMILIES
// DM Sans — UI, labels, titres
// Geist Mono — tous les chiffres financiers
//
// Téléchargées via Google Fonts à l'exécution.
// Certificats dans res/values/font_certs.xml.
// ─────────────────────────────────────────────

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val DmSansFont = GoogleFont("DM Sans")
private val GeistMonoFont = GoogleFont("Geist Mono")

internal val DmSans = FontFamily(
    Font(googleFont = DmSansFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = DmSansFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = DmSansFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = DmSansFont, fontProvider = provider, weight = FontWeight.Bold),
)

internal val GeistMono = FontFamily(
    Font(googleFont = GeistMonoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GeistMonoFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GeistMonoFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GeistMonoFont, fontProvider = provider, weight = FontWeight.Bold),
)

// ─────────────────────────────────────────────
// RÈGLE TYPOGRAPHIQUE
// DmSans → tout ce qui est UI (titres, labels, boutons, descriptions)
// GeistMono → tout ce qui est chiffre financier (montants, %, dates)
//
// Usage GeistMono dans un composant :
// Text(text = "4 200 €", style = MaterialTheme.typography.headlineLarge)
// → headlineLarge est défini avec GeistMono ci-dessous
// ─────────────────────────────────────────────

internal val FinanceOSTypography = Typography(
    // ── DISPLAY — montant hero (net worth, revenu du mois)
    displayLarge = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp,
    ),

    // ── HEADLINE — montants secondaires importants
    headlineLarge = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.3).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),

    // ── TITLE — titres d'écrans et de sections (UI, pas chiffres)
    titleLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),

    // ── BODY — texte courant, descriptions, notes
    bodyLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),

    // ── LABEL — labels de nav, chips, tags, section headers
    labelLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.08.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.1.sp,
    ),
)
