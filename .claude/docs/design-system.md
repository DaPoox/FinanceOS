# FinanceOS Design System

## Direction

Dark mode only. Navy deep. Gold accent. Revolut UX + Finary patrimoine feel. ADHD-friendly.  
Material Design 3 slots filled with our custom values.

---

## Colors

Defined in `Color.kt`. Three layers — palette → semantic tokens → composables.  
Never use raw hex or palette values in composables. Always go through `MaterialTheme`.

### M3 Slots

| Slot | Token | Value |
|---|---|---|
| `background` | `BackgroundDark` | `#090C12` |
| `surface` | `SurfaceDark` | `#0F1420` |
| `surfaceVariant` | `SurfaceVariantDark` | `#161D2E` |
| `surfaceContainer` | `SurfaceContainerDark` | `#1C263A` |
| `primary` | `PrimaryDark` | `#F0B429` |
| `onPrimary` | `OnPrimaryDark` | `#1A1000` |
| `onSurface` | `OnSurfaceDark` | `#E8EEF5` |
| `onSurfaceVariant` | `OnSurfaceVariantDark` | `#8BA4BE` |
| `outline` | `OutlineDark` | `rgba(255,255,255,0.06)` |
| `error` | `ErrorDark` | `#F87171` |

### Custom Tokens (`MaterialTheme.finColors`)

| Token | Value | Usage |
|---|---|---|
| `positive` | `#22C55E` | Positive deltas, OK indicators only |
| `warning` | `#FB923C` | Envelope between 80–100% of budget |
| `savings` | `#7EB8F7` | Savings charts and donuts |
| `investment` | `#A78BFA` | Investment charts and donuts |

### Color Gap Rule

If the HTML/JSX design file uses a color not present in `Color.kt`:  
→ use the closest existing token  
→ do NOT add new raw hex values  
→ flag the substitution in the response

---

## Typography

**DM Sans** — all UI text: titles, labels, buttons, descriptions  
**Geist Mono** — all financial figures: amounts, percentages, dates

Font files must be present in `res/font/` as `.ttf`:  
`dm_sans_regular`, `dm_sans_medium`, `dm_sans_semibold`, `dm_sans_bold`  
`geist_mono_regular`, `geist_mono_medium`, `geist_mono_semibold`, `geist_mono_bold`

### Role → Font mapping

| M3 Role | Font | Usage |
|---|---|---|
| `displayLarge` | Geist Mono | Hero amounts (net worth) |
| `headlineLarge/Medium/Small` | Geist Mono | Secondary financial figures |
| `titleLarge/Medium/Small` | DM Sans | Screen titles, section headers |
| `bodyLarge/Medium/Small` | DM Sans | Descriptions, notes |
| `labelLarge/Medium/Small` | DM Sans | Nav labels, chips, tags |

---

## Depth & Elevation

Depth through tonal stacking — no shadows except floating elements.

- Cards: `surfaceContainerLow` background, no borders
- Section separation: background color shift only, never a 1dp line
- Bottom sheet / inputs: `surfaceContainer`
- Floating elements (FAB, bottom nav): subtle diffused shadow

---

## Shapes (`Shape.kt`)

| Token | Radius | Usage |
|---|---|---|
| `extraSmall` | 8dp | Chips, badges |
| `small` | 12dp | Input fields, small cards |
| `medium` | 16dp | Cards, dialogs |
| `large` | 24dp | Modals, drawers |
| `extraLarge` | 32dp | FABs, full-bleed containers |

---

## Spacing (`Spacing.kt`)

4dp grid. Accessed via `MaterialTheme.spacing`.

| Token | Value |
|---|---|
| `xs` | 4dp |
| `sm` | 8dp |
| `md` | 16dp |
| `lg` | 24dp |
| `xl` | 32dp |
| `xxl` | 48dp |
| `xxxl` | 64dp |

---

## Design Source

Design was produced with Claude Design and delivered as HTML/JSX files.  
These files are the source of truth for layout, spacing, and visual intent.  
Implementation is done component by component from these files.
