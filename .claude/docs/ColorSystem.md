# Color System — From Palette to Composable

## The Three-Layer Rule

Data flows in one direction only — never backwards, never skipping steps.

```
┌─────────────────────────────────────────┐
│  LAYER 1 — Palette                      │
│  Raw color values. No meaning.          │
│  Navy900, Gold400, Emerald500           │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│  LAYER 2 — Semantic Tokens              │
│  Colors with meaning and purpose.       │
│  primary, savings, investment           │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│  LAYER 3 — Composables                  │
│  UI code. Never touches the palette.    │
│  MaterialTheme.colorScheme.primary      │
│  MaterialTheme.finColors.savings        │
└─────────────────────────────────────────┘
```

**Layer 1 (Palette):** Raw crayons. `internal` — composables can never reference them directly.  
**Layer 2 (Semantic):** Meaning without implementation. Swap the crayon, not the concept.  
**Layer 3 (Composables):** Ask for meaning, never for color.

---

## Two Types of Semantic Token

### Type A — M3 Role Colors

Standard Material Design 3 roles: `primary`, `surface`, `error`, etc.  
Wired via `darkColorScheme()` in `Theme.kt`. Components (Button, Card, etc.) read these automatically.

```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,          // Gold400
    background = BackgroundDark,    // Navy950
    surface = SurfaceDark,          // Navy900
    error = ErrorDark,              // Red400
    // ...
)
```

### Type B — Custom Domain Colors

Colors tied to data concepts, not UI roles.  
Exposed via `MaterialTheme.finColors` (a `FinanceColors` instance).

```kotlin
@Immutable
data class FinanceColors(
    val positive: Color,    // positive deltas, OK indicators
    val warning: Color,     // envelope 80–100% full
    val savings: Color,     // savings charts
    val investment: Color,  // investment charts
)

val MaterialTheme.finColors: FinanceColors
    @Composable get() = LocalFinanceColors.current
```

---

## Usage in Composables

```kotlin
// M3 role
Text(color = MaterialTheme.colorScheme.onSurface)
Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer))

// Custom domain token
val savingsColor = MaterialTheme.finColors.savings
Icon(tint = savingsColor)
Box(modifier = Modifier.background(savingsColor.copy(alpha = 0.12f)))
```

---

## Adding a New Color

1. **Add to palette** — `Color.kt`, `internal`
   ```kotlin
   internal val SomeColor = Color(0xFFXXXXXX)
   ```

2. **Add to semantic layer** — either `DarkColorScheme` (M3 role) or `FinanceColors` (domain token)
   ```kotlin
   // M3 slot
   private val DarkColorScheme = darkColorScheme(
       secondary = SomeColor,
   )

   // OR custom token
   LocalFinanceColors provides FinanceColors(
       savings = SomeColor,
       // ...
   )
   ```

3. **Use via MaterialTheme** — never skip step 2
   ```kotlin
   MaterialTheme.colorScheme.secondary
   MaterialTheme.finColors.savings
   ```

---

## Color Gap Rule (Design Implementation)

When translating HTML/JSX design files:  
- If a color in the design is not in `Color.kt` → use the closest existing token  
- Do NOT add new raw hex values  
- Flag the substitution explicitly

---

## Quick Reference

| ✅ Do | ❌ Don't |
|---|---|
| `MaterialTheme.colorScheme.primary` | `Color(0xFFF0B429)` |
| `MaterialTheme.finColors.savings` | `Palette.Blue300` |
| Keep palette `internal` | Make palette colors `public` |
| One semantic token per concept | Reuse `primary` for chart colors |
