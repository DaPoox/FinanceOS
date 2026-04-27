# TopAppBar — Spec

**Location:** `:presentation/core/designsystem/component/TopAppBar.kt`

---

## Base Component

A single parametrable composable. All slots are composables — not raw strings or icons.

```kotlin
@Composable
fun FinanceOSTopAppBar(
    modifier: Modifier = Modifier,
    startContent: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
)
```

- Slot is hidden when `null` — no empty space left behind
- Background follows the screen — no hardcoded color
- Height: standard `TopAppBarDefaults` height
- Don't forget the spacings.

---

## Preset Composables

Built on top of `FinanceOSTopAppBar`. Cover the common cases.

**SimpleTopAppBar** — back button + title text

```kotlin
@Composable
fun SimpleTopAppBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
)
```

**TitleOnlyTopAppBar** — centered title, no icons

```kotlin
@Composable
fun TitleOnlyTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
)
```

---

## Screen-Specific TopAppBar

If a screen has unique content in the bar (e.g. avatar + settings icon like Dashboard),
create a dedicated composable in that screen's package — not in design system.

```
:presentation/dashboard/DashboardTopBar.kt
```

---

## Rules

- Never pass a `String` or `ImageVector` directly — always wrap in a composable slot
- Presets live in design system — screen-specific bars live in the screen package
- No business logic in any TopAppBar composable