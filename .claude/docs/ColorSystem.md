# Color System — From Palette to Composable

## The Problem With Raw Colors

Imagine you're building a savings card. You need it green. Easy:

```kotlin
Box(Modifier.background(Color(0xFF4ADE80)))
```

Six months later, your designer says: "Savings should be teal now." You grep for `0xFF4ADE80`. It appears in 14 files. You change them all. You miss two. A bug report comes in.

This is the raw color problem. It's not about aesthetics — it's about **maintainability and meaning**.

---

## Three Layers, One Direction

A solid color system has three layers. Data flows in one direction only — never backwards, never skipping steps.

```
┌─────────────────────────────────────────┐
│  LAYER 1 — Palette                      │
│  Raw color values. No meaning.          │
│  Green400, Orange400, Neutral950        │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│  LAYER 2 — Semantic Tokens              │
│  Colors with meaning and purpose.       │
│  primary, savings, fixedExpenses        │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│  LAYER 3 — Composables                  │
│  UI code. Never touches the palette.    │
│  MaterialTheme.colorScheme.primary      │
└─────────────────────────────────────────┘
```

**Layer 1 (Palette):** Just crayons. `Green400 = Color(0xFF6EE591)`. No composable ever references this directly.

**Layer 2 (Semantic):** This is where meaning lives. `primary` doesn't say "green" — it says "the most important brand action color." `savings` doesn't say "orange" — it says "the color associated with the savings envelope category." You can change the underlying crayon without touching a single composable.

**Layer 3 (Composables):** Pure consumers. They ask "what's the savings color?" — not "give me orange."

---

## Two Types of Semantic Tokens

Not all semantic colors are the same. Knowing the difference shapes how you structure your system.

### Type A — M3 Role Colors

These are Material Design 3's standard semantic roles: `primary`, `secondary`, `surface`, `error`, etc. They describe **UI roles** — what element a color is used for, not what the data represents.

Use these for: buttons, backgrounds, text on surfaces, states (error, disabled).

```kotlin
// M3 roles — wired via darkColorScheme() / lightColorScheme()
internal val FinanceOSDarkColorScheme = darkColorScheme(
    primary    = Green400,
    secondary  = Blue300,
    background = Neutral950,
    error      = Red400,
    // ...
)
```

### Type B — Custom Domain Colors

These describe **what the data means**, not what UI role it plays. Category colors, chart segment colors, tag colors — these don't map to M3 roles. They need their own structure.

Use these for: donut chart segments, envelope category icons, transaction tags, any color tied to a specific data concept.

```kotlin
// Custom domain colors — outside M3's scope
data class FinanceOSCategoryColors(
    val fixedExpenses: Color,
    val investment: Color,
    val savings: Color,
    val other: Color,
)
```

---

## Dark & Light Themes — The Critical Part

This is where most developers get it wrong. They hard-code a color into the semantic token and call it done. But a color that reads perfectly on a dark background can be completely unreadable on a light one.

**The rule:** semantic tokens must be defined per theme. The same token (`savings`) points to different palette colors depending on the active theme.

For M3 roles, this is handled automatically:

```kotlin
// Dark theme
val FinanceOSDarkColorScheme = darkColorScheme(
    primary = Green400,   // brighter, works on dark bg
)

// Light theme (future)
val FinanceOSLightColorScheme = lightColorScheme(
    primary = Green900,   // deeper, works on light bg
)
```

For custom domain colors, you must handle this yourself by defining both variants:

```kotlin
// Dark — brighter, saturated, high contrast on dark surfaces
val FinanceOSDarkCategoryColors = FinanceOSCategoryColors(
    fixedExpenses = Green400,    // #6EE591 — bright green
    investment    = Blue300,     // #93C5FD — light blue
    savings       = Orange400,   // #FB923C — warm orange
    other         = Slate400,    // #8E9BA4 — muted grey
)

// Light — deeper, muted, high contrast on light surfaces
val FinanceOSLightCategoryColors = FinanceOSCategoryColors(
    fixedExpenses = Green900,    // deeper green
    investment    = Blue700,     // deeper blue
    savings       = Orange600,   // deeper orange
    other         = Slate600,    // deeper grey
)
```

Then expose via `CompositionLocal`, provided by the theme:

```kotlin
val LocalCategoryColors = staticCompositionLocalOf { FinanceOSDarkCategoryColors }

@Composable
fun FinanceOSTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) FinanceOSDarkColorScheme else FinanceOSLightColorScheme
    val categoryColors = if (darkTheme) FinanceOSDarkCategoryColors else FinanceOSLightCategoryColors

    CompositionLocalProvider(
        LocalSpacing provides FinanceOSSpacing(),
        LocalCategoryColors provides categoryColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = FinanceOSTypography,
            shapes      = FinanceOSShapes,
            content     = content,
        )
    }
}
```

Extend `MaterialTheme` for clean access, same pattern as `spacing`:

```kotlin
val MaterialTheme.categoryColors: FinanceOSCategoryColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCategoryColors.current
```

---

## Usage in Composables

With this in place, a composable never thinks about colors — it just asks for meaning:

```kotlin
@Composable
fun SavingsCard() {
    val color = MaterialTheme.categoryColors.savings

    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f))  // tinted background
    ) {
        Icon(tint = color)
        Text(color = color)
    }
}
```

Switch theme? Every card updates. Rebrand savings to teal? Change one line in `FinanceOSCategoryColors`. Zero composable changes.

---

## The Full Flow for Adding a New Color

1. **Add to palette** (`Color.kt`)
   ```kotlin
   internal val Orange400 = Color(0xFFFB923C)
   internal val Orange600 = Color(0xFFEA580C) // light theme variant
   ```

2. **Add to semantic structure** (`CategoryColors.kt` or `ColorScheme`)
   ```kotlin
   val FinanceOSDarkCategoryColors = FinanceOSCategoryColors(
       savings = Orange400,  // mapped here
       // ...
   )
   val FinanceOSLightCategoryColors = FinanceOSCategoryColors(
       savings = Orange600,  // different shade for light
       // ...
   )
   ```

3. **Use in composable via MaterialTheme**
   ```kotlin
   MaterialTheme.categoryColors.savings
   ```

Never do step 3 without step 2. Never do step 2 by pointing at a raw hex.

---

## Quick Reference

| ✅ Do | ❌ Don't |
|---|---|
| `MaterialTheme.colorScheme.primary` | `Color(0xFF6EE591)` |
| `MaterialTheme.categoryColors.savings` | `Orange400` |
| Define both dark + light values | Use one value for both themes |
| Keep palette `internal` | Make palette colors `public` |
| One semantic token per concept | Reuse `primary` for category colors |

---

## Why `internal` on Palette Colors?

Notice all palette colors are `internal`:

```kotlin
internal val Orange400 = Color(0xFFFB923C)
```

This is intentional. Making them `internal` means they can only be referenced within the `:presentation` module — composables in feature packages can't accidentally import them. The only path into a composable is through `MaterialTheme`. The compiler enforces the rule you care about.
