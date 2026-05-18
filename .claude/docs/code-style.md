# Code Style

## Formatter

**ktfmt** — Google style. Non-negotiable. All code must pass ktfmt before commit.

Setup in Android Studio: Settings → Editor → Code Style → Kotlin → Set from... → ktfmt

---

## General Rules

- No vertical alignment of `=` or `:` — one space after, always
- No manual column formatting — let ktfmt handle it
- Trailing commas on multiline parameter lists — always
- Max line length: 120 characters
- No wildcard imports (`import com.example.*`)

---

## Named Parameters

Always use named parameters for functions with 2+ parameters of the same type,
or when the meaning is not obvious from position.

```kotlin
// Good
Text(
    text = "Hello",
    color = Color.White,
)

// Bad — what does true mean?
SomeFunction(true, false, true)
```

---

## Composables

One parameter per line when 2+ parameters are passed. Trailing comma on last param.

```kotlin
// Good
Text(
    text = "WEALTH",
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.primary,
)

// Bad — vertical alignment
Text(
    text      = "WEALTH",
    style     = MaterialTheme.typography.labelSmall,
    color     = MaterialTheme.colorScheme.primary,
)

// Bad — everything on one line when it's long
Text(text = "WEALTH", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
```

Modifier chains — one call per line when chain has 2+ calls:

```kotlin
// Good
Modifier
    .fillMaxWidth()
    .padding(16.dp)
    .background(color, RoundedCornerShape(8.dp))

// Bad
Modifier.fillMaxWidth().padding(16.dp).background(color, RoundedCornerShape(8.dp))
```

---

## Conditionals

Use braces for if/else. No ternary-style alignment tricks.

```kotlin
// Good
val color = if (state.isOverAllocated) {
    MaterialTheme.colorScheme.tertiary
} else {
    MaterialTheme.colorScheme.primary
}

// Bad — fake alignment
val color = if (state.isOverAllocated) MaterialTheme.colorScheme.tertiary
            else                       MaterialTheme.colorScheme.primary
```

Single-line `if` only when the entire expression fits on one line under 120 chars:

```kotlin
val label = if (isActive) "ON" else "OFF"
```

---

## Naming

| Thing | Convention | Example |
|---|---|---|
| Composable | `PascalCase` | `FinanceOSBottomNav` |
| ViewModel | `<Screen>ViewModel` | `DashboardViewModel` |
| UiState | `<Screen>UiState` | `DashboardUiState` |
| UiAction | `<Screen>UiAction` | `DashboardUiAction` |
| UiEvent | `<Screen>UiEvent` | `DashboardUiEvent` |
| Ui model | `<Model>Ui` | `TransactionUi` |
| Constants | `SCREAMING_SNAKE_CASE` | `MAX_BUDGET_AMOUNT` |
| Extension file | context-based | `CurrencyExtensions.kt` |
| Dp/Sp values | suffix with type | `cornerRadiusDp`, `fontSizeSp` |

---

## Comments

Comment intent, not mechanics. One line above the block it describes.

```kotlin
// Fallback for API < 31 — BlurEffect not available below
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    Modifier.graphicsLayer { renderEffect = BlurEffect(20f, 20f) }
} else {
    Modifier.background(Color(0xCC0F1417))
}
```

KDoc on all public functions and classes:

```kotlin
/**
 * Pill-shaped progress bar with gradient fill.
 * Used in the dashboard header for monthly and weekly budget tracking.
 */
@Composable
fun GradientProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
)
```

---

## Rules

- No vertical `=` alignment — ever
- ktfmt formats, you don't
- Trailing commas on all multiline lists
- Braces on all if/else unless single-line fits under 120 chars
- Named parameters when meaning is not obvious from position