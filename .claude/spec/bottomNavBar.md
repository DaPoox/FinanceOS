# Bottom Navigation Bar — Spec

**Figma node:** `16:292` — `BottomNavBar Shell`  
**File:** `FinanceOSBottomNav.kt`  
**Location:** `:presentation/core/designsystem/component/`

---

## Visual Reference

Dark frosted glass bar anchored to the bottom of the screen. Rounded top corners.
4 tabs laid out horizontally. Active tab is a green pill with dark text. Inactive tabs are icon + label with muted color, no background.

---

## Container

| Property | Figma value | Compose token |
|---|---|---|
| Background | `rgba(15,20,23,0.8)` | `Neutral950` at 80% alpha |
| Blur | `backdrop-blur-[20px]` | `BlurEffect(20f, 20f)` (API 31+), solid fallback below |
| Top corners | `rounded-tl-[48px] rounded-tr-[48px]` | `RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)` |
| Bottom corners | none (square into safe area) | `0.dp` |
| Padding | `pt-[16px] pb-[32px] px-[16px]` | `top=16.dp, bottom=32.dp, horizontal=16.dp` |
| Shadow | `0px -10px 40px rgba(0,0,0,0.4)` upward | `Modifier.shadow` or custom `drawBehind` |
| Width | full width | `fillMaxWidth()` |
| Layout | row, items equally spaced | `Row`, `Arrangement.SpaceEvenly` |

---

## Tabs

| Tab | Label | Active icon size | State |
|---|---|---|---|
| WEALTH | `WEALTH` | 19×18dp | Default active |
| ASSETS | `ASSETS` | 18×18dp | Inactive |
| GROWTH | `GROWTH` | 20×12dp | Inactive |
| VAULT | `VAULT` | 16×21dp | Inactive |

---

## Active Tab

| Property | Value | Token |
|---|---|---|
| Background | `#6EE591` pill | `Green400`, `RoundedCornerShape(50%)` |
| Padding | `px-[24px] py-[8px]` | `horizontal=24.dp, vertical=8.dp` |
| Icon color | `#0F1417` | `Neutral950` |
| Label color | `#0F1417` | `Neutral950` |

---

## Inactive Tab

| Property | Value | Token |
|---|---|---|
| Background | none | — |
| Padding | `px-[24px] py-[8px]` | `horizontal=24.dp, vertical=8.dp` |
| Icon color | `#64748B` | ⚠️ not in current palette — closest is `Slate400` (`#8E9BA4`). Add `Slate500 = Color(0xFF64748B)` to `Color.kt` |
| Label color | `#64748B` | same as above |

---

## Labels

| Property | Value |
|---|---|
| Font | Inter |
| Size | `10sp` → `labelSmall` from `FinanceOSTypography` |
| Weight | Bold |
| Case | Uppercase |
| Letter spacing | `0.5sp` |
| Gap from icon | `4dp` |

---

## Icons

Figma uses custom SVG assets (not bundled yet). Use Material Icons as placeholders:

| Tab | Placeholder icon |
|---|---|
| WEALTH | `Icons.Default.Home` |
| ASSETS | `Icons.Default.AccountBalance` |
| GROWTH | `Icons.Default.TrendingUp` |
| VAULT | `Icons.Default.Lock` |

Replace with real assets in a future task once icons are added to the project.

---

## Component API

```kotlin
enum class DashboardTab { WEALTH, ASSETS, GROWTH, VAULT }

@Composable
fun FinanceOSBottomNav(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
)
```

---

## Color Gap to Fix

The inactive label/icon color `#64748B` is not in `Color.kt`.  
**Action:** add `internal val Slate500 = Color(0xFF64748B)` before implementing.

---

## Previews

- One `@Preview` per tab state (4 total), or a single preview with `WEALTH` selected.
- All wrapped in `FinanceOSTheme`.
