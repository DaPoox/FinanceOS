# Dashboard Top Bar — Spec

**Figma node:** `16:280` — `Header - TopAppBar Shell`  
**File:** `DashboardTopBar.kt`  
**Location:** `:presentation/core/designsystem/component/`

---

## Visual Reference

Frosted glass bar pinned to the top of the screen. Left side: circular profile avatar + app title. Right side: circular settings icon button.

---

## Container

| Property | Figma value | Compose mapping |
|---|---|---|
| Background | `rgba(15,20,23,0.6)` | `surface.copy(alpha = 0.6f)` |
| Blur | `backdrop-blur-[12px]` | Approximated via alpha (Compose limitation) |
| Padding | `px-[24px] py-[16px]` | `horizontal=24.dp, vertical=16.dp` |
| Layout | row, space between | `Row`, `Arrangement.SpaceBetween` |
| Width | full width | `fillMaxWidth()` |

---

## Left Side — Profile + Title

| Element | Property | Figma value | Compose mapping |
|---|---|---|---|
| Row gap | gap | `12dp` | `Arrangement.spacedBy(12.dp)` |
| Avatar | size | `40×40dp` | `Modifier.size(40.dp)` |
| Avatar | shape | pill | `CircleShape` |
| Avatar | background | `#303539` | `surfaceContainerHighest` ⚠️ closest existing token |
| Avatar | content | profile image | placeholder `Box` (no image asset yet) |
| Title | text | "Emerald Ledger" | hardcoded string |
| Title | color | `#6ee591` | `MaterialTheme.colorScheme.primary` |
| Title | size | `18sp` | custom `TextStyle` |
| Title | weight | ExtraBold | `FontWeight.ExtraBold` |

---

## Right Side — Settings Button

| Property | Figma value | Compose mapping |
|---|---|---|
| Size | `40×40dp` | `Modifier.size(40.dp)` |
| Shape | pill | `CircleShape` |
| Background | `#1b2023` | `surfaceContainerLow` ⚠️ closest existing token |
| Icon | settings/gear, `20×20dp` | `Icons.Default.Settings`, `Modifier.size(20.dp)` |
| Icon color | — | `onSurfaceVariant` |

---

## Component API

```kotlin
@Composable
fun DashboardTopBar(modifier: Modifier = Modifier)
```

No state needed at this stage — title and avatar are static.

---

## Notes

- Color deviations from Figma accepted by user — use closest existing tokens only.
- Profile image is a placeholder until asset pipeline is set up.
- Settings button has no action wired yet.
