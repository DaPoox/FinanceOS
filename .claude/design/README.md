# Handoff — Finance OS

> Mobile-first personal finance app — envelope-based budgeting, net worth tracking, monthly allocation flow, expense capture.

---

## 1. Overview

**Finance OS** is a French-language personal finance app for someone who:
- Manages a monthly **budget** by allocating their income to **envelopes** (Fixes, Variables, Du mois, Permanentes, Épargne, Investissement).
- Tracks **net worth (Patrimoine)** across multiple accounts (compte courant, épargne, investissements).
- Logs **expenses** quickly via a bottom-sheet numpad.
- Reviews monthly **history** to see contribution / saving-rate trends.

Core mental model — every euro of income is assigned to an envelope at the start of the month. Daily expenses draw down those envelopes. Anything left at end-of-month rolls into "Permanentes" (long-term goals) or savings/invest contributions.

The product is one app with four bottom-nav pillars: **Accueil**, **Budget**, **Patrimoine**, **Historique**, plus two overlay flows (envelope detail, monthly allocation) and one bottom sheet (expense entry).

---

## 2. About the design files

The files in `design/` are **design references created in HTML + React (via Babel-standalone, inline JSX)**. They are prototypes showing the intended **look and behaviour**, not production code to ship as-is.

Your task is to **recreate these designs in the target codebase's existing environment** (React Native, SwiftUI, Flutter, Kotlin, web React, etc.) using its established patterns:
- Match colours, typography, spacing, radii, shadows, motion exactly.
- Reuse the codebase's existing primitives (Button, Card, ProgressBar, etc.) where they exist; do NOT copy the inline-style approach from the prototype.
- If no codebase / framework has been chosen yet, pick the most appropriate one for a mobile-first app (React Native or SwiftUI/Jetpack Compose are natural fits — the design is sized 412 × 892 for a typical Android viewport).
- Replace mock data (`design/data.jsx`) with real data fetched from your backend / state management.

**The prototype is for visual fidelity and interaction reference. The architecture in the implementation is your call.**

---

## 3. Fidelity

**High-fidelity (hifi).** The prototype is pixel-locked: every colour, font size, radius, spacing value, and animation easing in this README has been pulled directly from the source files. Treat them as ground truth. Where the README says `radius: 20`, that means `border-radius: 20px`. Where it says `DM Sans 500 / 14px`, that means font-family DM Sans, weight 500, size 14px.

---

## 4. Design tokens

All tokens live in `design/primitives.jsx` (`FOS` object). Reproduce them as design-system constants (CSS vars / theme tokens / Swift enum / Kotlin object — whatever the codebase uses).

### 4.1 Colours

| Token | Hex | Usage |
|---|---|---|
| `bg` | `#090c12` | App background, behind cards |
| `surface` | `#0f1420` | Default card background |
| `surfaceVar` | `#161d2e` | Slightly raised surface (chips, inner buttons) |
| `surfaceCt` | `#1c263a` | Bottom nav, sticky footers (most elevated) |
| `primary` | `#f0b429` | Brand accent (amber/gold). FAB, active nav, primary CTA, sparklines |
| `onPrimary` | `#1a1000` | Text/icon on top of `primary` |
| `text` | `#e8eef5` | Primary text (off-white) |
| `textDim` | `#8ba4be` | Secondary text, labels, dim icons |
| `outline` | `rgba(255,255,255,0.06)` | Default 1px border on cards/rows |
| `outlineSt` | `rgba(255,255,255,0.10)` | Stronger 1px border (buttons, inputs) |
| `err` | `#f87171` | Over-budget, errors, negative amounts |
| `pos` | `#22c55e` | Positive amounts, "Bon" status, gains |
| `warn` | `#fb923c` | Warning / nearing limit |
| `save` | `#7eb8f7` | Savings category accent |
| `invest` | `#a78bfa` | Investment category accent |
| (extra) | `#4a5568` | "Marché" / liquidity slice in donut |

Shell phone bezel gradient: `linear-gradient(180deg, #1a1f2c 0%, #0a0d14 100%)`.

Pill backgrounds are typically `{color}1F` (12% opacity) over surface — for example a primary pill uses `#f0b4291F`.

### 4.2 Typography

Two families, loaded from Google Fonts:

```html
<link href="https://fonts.googleapis.com/css2?family=DM+Sans:opsz,wght@9..40,400;9..40,500;9..40,600;9..40,700&family=Geist+Mono:wght@400;500;600;700&display=swap" rel="stylesheet">
```

- **DM Sans** — UI text (labels, body, button text). Weights used: 400, 500, 600, 700.
- **Geist Mono** — All numerals (`<Num>` primitive). Weights used: 400, 500, 600. Always with `font-feature-settings: "tnum" on, "lnum" on` (tabular lining figures) and `letter-spacing: -0.2px`.

Type scale (every size in px):

| Role | Family | Size | Weight | Letter-spacing | Notes |
|---|---|---|---|---|---|
| Hero number (net worth) | Geist Mono | 40 | 500 | -1.2 | Home + Patrimoine |
| Hero number (envelope detail) | Geist Mono | 36 | 500 | -0.2 | Envelope detail |
| Numpad display | Geist Mono | 56 | 500 | -2 | Expense sheet, income step |
| Screen title | DM Sans | 22 | 600 | 0 | Budget, Historique, Patrimoine page title |
| Header title (in `ScreenHeader`) | DM Sans | 18 | 600 | 0 | Envelope detail, Allocation |
| Body / card title | DM Sans | 14 | 500 | 0 | Envelope name, account name, month name |
| Big stat | Geist Mono | 28 | 500 | -0.2 | Restant à dépenser, totaux |
| Mid stat | Geist Mono | 20 | 500 | -0.2 | Annual stats |
| Inline numeric | Geist Mono | 14–15 | 500 | -0.2 | Row totals |
| Label / secondary | DM Sans | 12 | 400 | 0 | Subtitles, captions |
| Section label (uppercase) | DM Sans | 11 | 600 | 1.4 | All caps, color `textDim` |
| Micro label | DM Sans | 10–11 | 500 | 0.8 (when uppercase) | Sub-captions inside cards |
| Pill | DM Sans | 12 | 600 | 0.2 | Tiny pills drop to 9–11 |

The reusable `SectionLabel` component (uppercase, tracked, dim) is the canonical "section header" — use it everywhere a small all-caps category title is needed.

### 4.3 Spacing

The grid is loose. Common spacings:

- Screen horizontal padding: **20px** (left/right). Inner card padding: **16–20px**.
- Vertical rhythm between sections: **18–22px** (`padding: '20px 20px 0'` is the most common screen-section start).
- Row gap inside a list of cards: **6–10px**.
- Form / row vertical padding: **12–14px**.
- Bottom-nav reservation: every screen has `paddingBottom: 100` to clear the floating nav.

### 4.4 Radii

| Element | Radius |
|---|---|
| Phone bezel (outer) | 44px |
| Phone screen (inner) | 36px |
| Bottom sheet (top corners only) | 28px |
| FAB | 22px |
| Big card | 20px |
| Standard card | 16px (envelope row), 18px (tx group) |
| Small card / input / button | 14px |
| Chip / row icon | 8–12px |
| Pill | 100px (fully rounded) |
| Back button, nav pill, avatar | 100px |
| Progress bar | equal to bar height (so it's a stadium) |

### 4.5 Shadows / elevation

Used sparingly:

- **Phone bezel:** `0 40px 80px rgba(0,0,0,0.55), inset 0 0 0 1px rgba(255,255,255,0.06)`
- **FAB:** `0 12px 24px ${primary}55, 0 0 0 6px ${bg}` (the second shadow creates the "punch-through" ring effect against the bottom nav)
- Cards do **not** use box-shadow — they rely on the `outline` border + the `surface` colour stepping up from `bg`.

### 4.6 Motion

| Animation | Duration | Easing |
|---|---|---|
| Count-up (hero numbers) | 900–1100ms | Cubic ease-out (`1 - (1-p)^3`), 30 steps |
| Progress bar fill | 900ms | `cubic-bezier(0.22, 1, 0.36, 1)` (entry: 60ms delay; per-row stagger 30–40ms) |
| Donut stroke fill | 1000ms | `cubic-bezier(0.22, 1, 0.36, 1)` |
| FAB press | 180ms | `cubic-bezier(0.34, 1.56, 0.64, 1)` (overshoot). Scale 1 → 0.94 |
| Bottom sheet slide | 420ms | `cubic-bezier(0.32, 0.72, 0, 1)` |
| Scrim fade | 240ms | linear |
| Tab pill background | 220ms | ease |
| Confirmation pop | 380ms | `cubic-bezier(0.34, 1.56, 0.64, 1)` |
| Numpad key press | 80ms | ease (scale 1 → 0.97) |
| Allocation step indicator pill width | 240ms | ease |

Screen-to-screen transitions in the prototype are intentionally **disabled** (kept as static cross-fades) because they were unreliable in the preview. Re-add native push/modal transitions in the target platform's idiomatic style — push-from-right for envelope detail, present-from-bottom for the allocation flow.

---

## 5. Screens

The prototype renders at **412 × 892** (Android-ish viewport). All measurements assume that frame.

### 5.1 Phone shell + chrome (`phone.jsx`)

Every screen renders inside a `PhoneShell` which provides:

- **Status bar** — 36px tall, dark, time on the left (`9:41`, DM Sans 13 / 500), centred camera notch (22 × 22 black circle, offset 8px from top), signal + wifi + 78%-filled battery on the right.
- **Content area** — flexes to fill remaining height; children manage their own scroll.
- **Gesture bar** — 22px tall, centred 128 × 4 pill at 40% white.

Top-level layout per screen: vertical scroll, bottom padding of 100 to clear the nav.

### 5.2 Bottom navigation

- Background `surfaceCt`, `borderTop: 1px outline`, padding `8px 12px 12px`, fixed at bottom of phone screen.
- Four tabs, equally distributed (flex 1, justify-content space-around):
  1. **Accueil** — `home` — house icon
  2. **Budget** — `budget` — calendar icon
  3. **Patrimoine** — `patrimoine` — wallet icon
  4. **Historique** — `history` — clock icon
- Icons are 22 × 22 stroke 1.6, drawn from custom SVG paths (no icon library). All four are in `phone.jsx`.
- Active tab shows a 56 × 26 pill at `primary` 12% behind the icon and switches icon + label colour to `primary`. Label weight 600 when active, 500 otherwise. Label 11px DM Sans, 4px gap below icon.
- The nav is **hidden** when an overlay (envelope, allocation) is active.

### 5.3 Floating Action Button (FAB)

- Visible only on **Accueil** and **Budget** tabs (and never with an overlay open).
- Position: `right: 20, bottom: 92` (sits above the bottom nav).
- 60 × 60 square with `borderRadius: 22`, background `primary`, plus icon stroke 2.5 in `onPrimary`.
- Shadow described in §4.5. On press, scales to 0.94.
- Opens the **expense bottom sheet** (`screen-expense.jsx`).

### 5.4 Accueil / Home (`screen-home.jsx`)

Vertical list of cards. Top to bottom:

1. **Greeting row** — "Bonjour, Théo" (DM Sans 13, `textDim`) over "Mai 2026" (DM Sans 18 / 600). Right side: 40 × 40 circular bell button (`surface` bg, `outline` border).
2. **Hero card — Patrimoine total.**
   - `SectionLabel`: "Patrimoine total".
   - Big mono number (animated count-up over 1100ms) + " €" in `textDim` at 20px.
   - Primary pill with up-triangle: `+1 840 € ce mois` + dim caption "Meilleur mois depuis 4 mois 🔥".
   - Three contribution rows: Épargne / Investissement / Marché. Each row = 92px label + flex progress bar (5px) + 50px right-aligned value (`+{value} €`).
3. **Insight card** — left-border-accent `warn` orange (3px wide), 32 × 32 alert icon in warn-tinted square. Headline "Restos dépassé de **26 €**" + subtitle "5 sorties ce mois. Tu en es à 112% du budget."
4. **Budget mai card** — `SectionLabel` row with right-side caption "4 200 € de revenu". Card shows "Restant à dépenser" big mono + right side "Dépensé X / Y €" + 8px progress bar in `primary`. Tap → switch to Budget tab.
5. **Enveloppes actives** — 2 × 2 grid of `EnvelopeMini` cards. Top-4 most-spent envelopes (excluding Permanentes / Épargne / Investissement). Each mini card has: 32 × 32 rounded icon, optional status pill (`DÉPASSÉ` red, `FIXE` neutral), envelope name, big mono spent / dim "/ allocated €", 4px progress.
6. **Patrimoine 6 mois card** — `SectionLabel` "Patrimoine 6 mois" + right `+11.8%` green pill. Sparkline 340 × 92 in primary with gradient fill underneath (`primary` at 15% → 0%). Below: 6 month labels (déc, jan, fév, mar, avr, mai) at 10px dim.
7. **Mois récents** — 2 cards (months 1 + 2 from history, not "mai"). Each row shows month name (capitalized), "Revenu X €", and right side "PATRIMOINE +Y €" in green + status dot. Tap → Historique tab.

### 5.5 Budget (`screen-budget.jsx`)

1. **Header** — "Budget" (13 dim) / "Mai 2026" (22 / 600). Right side: "Allouer" pill button with stacked-sliders icon. Tap → opens **Allocation overlay**.
2. **Global progress card** — "Revenu — Dépenses" caption + big mono "X € restant" / right "Y €" income label. 8px primary progress (spent / income). Bottom row: "X € dépensé" / "Y € alloué".
3. **Envelope groups** — for each of 6 groups (Fixes, Variables, Du mois, Permanentes, Épargne, Investissement):
   - `SectionLabel` with group total on the right.
   - List of `EnvelopeRow` cards (1px outline, 16 radius, padding 12 × 14). Each row:
     - 38 × 38 rounded icon square (12 radius) — coloured per type: save → blue, inv → purple, fixe → dim, others → text.
     - Name (DM Sans 14 / 500) + optional inline pills (`NEW` for du-mois, `∞` for permanentes, `DÉPASSÉ` red for over-budget).
     - 4px progress bar (color depends on state: err → warn → save/invest accent → text).
     - Right column 78px min: for normal envelopes "X / Y €", for permanentes "{accumulated} €" + "+X €/mois" subtitle.
   - Rows stagger their progress fill by `i * 40ms`.

### 5.6 Envelope detail overlay (`screen-envelope.jsx`)

Pushed in from the right when an envelope row / mini-card is tapped. Fills the phone area, hides the bottom nav.

- `ScreenHeader` with 40 × 40 round back button (left), title (envelope name, 18 / 600) + subtitle (`typeLabel(type)` — e.g. "Variable standard", "Permanente • accumulée").
- **Hero status card.**
  - `SectionLabel` "Dépensé" (or "Accumulé" for permanentes).
  - Big mono `spent` (36px), " €" dim 18. Caption "sur X € alloués".
  - Top-right status `Pill`: "OK" (pos), "Attention" (warn), "Dépassé" (err).
  - 10px progress bar. Below: "X% utilisé" / "Y € restants" (or "+Z € de dépassement" in err).
  - Full-width secondary button "Modifier l'allocation" — `surfaceVar` bg, `outlineSt` border, edit icon + label.
- **Transactions** — `SectionLabel` "Ce mois" + right "{n} transactions". Single grouped card (radius 18, hidden overflow) with rows separated by `outline` dividers. Each row: note (14) + date (11 dim) — right side mono "−X €".
- **For Permanentes only:** an extra "Historique mensuel" card with a sparkline showing accumulation curve, "Démarré il y a 8 mois" caption + "+{accumulated} €" in green.

### 5.7 Allocation overlay (`screen-allocation.jsx`)

Three-step modal flow presented from the bottom. `ScreenHeader` shows a step indicator (3 pills, active one widens to 20px in primary, others 6px in surfaceVar) on the right.

**Step 1 — "Quel est ton revenu ce mois ?"**
- Big centered mono `<input>` (56px, letter-spacing -2) with caret in primary. Custom underline (160 × 1, gradient transparent → primary → transparent).
- 4 preset chips below: 4 000, 4 200, 4 500, 5 000 — fully rounded, active chip uses primary tint.

**Step 2 — "Sur quelle base partir ?"**
- 4 radio cards (full width). Each: 22 × 22 radio circle (`outlineSt` border, primary border + inner 10px dot when active) + title + subtitle.
- Active card has primary border + `primary` 8% bg tint.
- Options: "Mois précédent" (with `Conseillé` pill), "Un mois passé", "Template par défaut", "From scratch".

**Step 3 — "Ajuste tes enveloppes"**
- All 6 groups rendered as in §5.5, but each envelope is a thin row (radius 14, padding 10 × 14) with: 28 × 28 icon, name, right-aligned 64px numeric input (`surfaceVar` bg, `outline` border, radius 8) + " €".

**Sticky footer** at the bottom of the phone screen — `surfaceCt`, 1px top border, padding `14px 20px 18px`.
- On step 3 only: "Non alloué" label + remaining amount, coloured pos / warn / err depending on sign/value.
- Big primary button: "Continuer" (steps 0 / 1) or "Valider l'allocation" (step 2). Full-width, 16 padding, weight 700, letter-spacing 0.3.

### 5.8 Patrimoine (`screen-patrimoine.jsx`)

1. **Header** — "Patrimoine" (13 dim) over big mono total (40 / 500) + " €" in 22 dim. Below: two pills — "+12 380 € · 6 mois" in pos green + "+11.8%" in pos green.
2. **Donut card** — `SectionLabel` "Répartition". Layout: 150 × 150 donut left (stroke 14, gap 3, slices in save / invest / `#4a5568` for liquidity) with centre "{k}k" + "TOTAL" micro-caption; right side: 3 `Legend` rows — colour dot, label, % on the right, value below at 15 / 500.
3. **Évolution 12 mois card** — `SectionLabel` + 3 timeframe chips (6M / 12M / 3A; 12M is active — `surfaceVar` bg + text colour). Sparkline 340 × 120 in primary.
4. **Comptes section** — `SectionLabel` "Comptes" with right-side "+ Ajouter" link in primary 600. Three sub-sections by type (Compte courant / Épargne / Investissement), each with a small dim caption then a stack of `AccountRow` cards.
   - `AccountRow`: 34 × 34 rounded square avatar (account colour 12% bg + colour text, 2-letter abbreviation, Geist Mono 13 / 600), name + cap caption (when capped), right balance.
   - If `cap` is set (Livret A, LDDS), a 4px progress bar + dim "X% de capacité" / "Y € disponibles" appears below.

### 5.9 Historique (`screen-history.jsx`)

1. **Header** — "Historique" (13 dim) / "12 derniers mois" (22 / 600).
2. **Bilan annuel card** — gradient bg (`linear-gradient(135deg, surface 0%, surfaceVar 100%)`). 2-col grid of stats (Revenu total, Patrimoine ajouté — second value with `+` sign and pos colour). Below a 1px top border: "Taux d'épargne moyen" (12 dim) over big primary number "X%" (26 / 500) on the left; a mini 8-bar chart on the right (bars 6px wide, gap 3, height 36, increasing opacity).
3. **Tous les mois list** — `MonthRow` for each entry. Each row has:
   - 4 × 36 left-edge colour bar (status colour, opacity 0.9).
   - Month name (capitalised, DM Sans 14 / 500) + optional `RECORD` primary pill (status `best`).
   - Two captions in a row: "{income} revenu" / "{spent} dépensé" with the numeric in `text` and the word in `textDim`.
   - Right side: "+{contrib}" in pos green (15 / 500) + " €" in 11 dim.
   - Status colours: `best` → primary, `good` → pos, `mid` → warn, `hard` → err.

### 5.10 Expense bottom sheet (`screen-expense.jsx`)

Triggered by FAB. Slides up from below the gesture bar.

- **Scrim** — rgba(0,0,0,0.55), fades in 240ms.
- **Sheet** — `surface` bg, top corners rounded 28, top border 1px `outlineSt`. Slides in 420ms with the steep cubic-bezier above.
- **Grabber** — 38 × 4 pill, 18% white, top-centred.
- **Header** row — "Nouvelle dépense" (13 dim) left, X close button right.
- **Amount display** — big mono 56px, " €" 28 dim. Shows "0" in dim when empty.
- **Envelope chips row** — horizontal scroll. Default candidates: groceries, restos, transport, shopping, ams, vacances. Active chip → primary border + primary 12% bg + primary text. Each chip has the envelope icon glyph + name.
- **Note input** — full-width `surfaceVar` input, radius 14, 14px DM Sans, placeholder "Note (optionnelle)".
- **Numpad** — 4-column grid, 8px gap.
  - Keys 1–9, 0, comma, double-zero (00), backspace (⌫), clear (C). All 52px tall, radius 16, `surfaceVar` bg, Geist Mono 22 / 500.
  - Right column = single tall **Validate** button (`gridRow: span 4`, radius 20, primary bg, checkmark icon stroke 2.4). Disabled state uses 18% primary bg + dim text.
  - On press, keys scale to 0.97 and brighten to `surfaceCt`.
- **Confirmation state** — once Validate fires, the amount area swaps to a pop-animated checkmark badge (64 × 64 round, pos 12% bg, big check icon) + caption "**X €** sur {envelope.name}". After 700ms the sheet calls `onSave` and closes.

---

## 6. Reusable primitives

These are the building blocks (`design/primitives.jsx`). Re-implement each as a real component in the target codebase.

| Primitive | Props | Purpose |
|---|---|---|
| `Card` | `variant: 'surface'\|'variant'`, `style`, `onClick` | Default container — `surface` or `surfaceVar` bg, 1px outline, radius 20, padding 16 |
| `Progress` | `value`, `max`, `color`, `bg`, `height` (default 6), `animate`, `delay` | Stadium bar; auto-switches to `err` colour + right-edge spike when `value > max`; animates width on mount |
| `Pill` | `color` (text + auto-bg at 12%), `bg`, custom content | 12px DM Sans 600, fully rounded, 6 × 10 padding |
| `Num` | `size`, `weight`, `color` | Geist Mono span with tabular figures, -0.2 tracking. Use for every number in the UI |
| `SectionLabel` | `children`, `right` | Uppercase 11px DM Sans 600 tracked label, optional right-aligned content |
| `Sparkline` | `data`, `width`, `height`, `stroke`, `fillFrom`, `fillTo`, `showDot` | Smooth area chart, cardinal-ish curve, gradient fill, end-of-line marker dot (concentric circles in primary + bg core) |
| `Donut` | `slices: [{value, color}]`, `size`, `stroke`, `gap`, `center` | Stroke-only donut, rotated -90°, stroke-dasharray animation, supports a centred React node |
| `useCountUp(target, opts)` | hook | Animates a number from 0 → target over `ms`, with `steps` increments and cubic ease-out |

Other reusable pieces (defined inside screen files but reusable as components): `EnvelopeMini` (home grid), `EnvelopeRow` (budget list), `AccountRow`, `MonthRow`, `Legend`, `Stat`, `BarsMini`, `Key` / `KeyValidate` (numpad).

---

## 7. Navigation / state model

The app is a single-page React tree with a tiny manual state machine — port this to the platform's standard navigation library (React Navigation stack + modal, SwiftUI NavigationStack + sheet, etc.).

State shape (from `app.jsx`):

```js
{
  tab: 'home' | 'budget' | 'patrimoine' | 'history',
  overlay: null | { kind: 'envelope', id } | { kind: 'allocation' },
  sheet: boolean, // expense sheet open
}
```

Transitions:

- `BottomNav.onChange(id)` → set `tab`. Bottom nav visible iff `overlay === null`.
- `onOpenEnvelope(id)` from Home or Budget → `overlay = { kind: 'envelope', id }`.
- `onOpenAllocation()` from Budget header or Home shortcut → `overlay = { kind: 'allocation' }`.
- `onBack` from overlay → `overlay = null`.
- `FAB.onClick` → `sheet = true`. FAB shown only when `tab ∈ {home, budget}` and no overlay.
- `ExpenseSheet.onClose` → `sheet = false`. Sheet's internal `amount/envelope/note/confirmed` state resets 250ms after close.
- Allocation flow has internal `step ∈ {0, 1, 2}` and back behaviour: header's back goes to previous step until step 0, where it dismisses the overlay.

---

## 8. Data model

The prototype uses static mock data in `design/data.jsx`. Replace with your real schemas. Shape reference:

```ts
type EnvelopeType = 'fixe' | 'var' | 'month' | 'perm' | 'save' | 'inv';

interface Envelope {
  id: string;
  name: string;            // FR display name
  type: EnvelopeType;
  icon: string;            // single glyph — replace with real icon enum / asset id
  allocated: number;       // EUR, integer
  spent: number;           // EUR, integer
  color: string;           // hex — drives row accent
  accumulated?: number;    // perm only: lifetime accumulated balance
}

interface Transaction {
  date: string;            // "DD mois" — short FR
  note: string;
  amount: number;          // positive EUR; rendered as "−X €"
}

interface Account {
  name: string;
  type: 'Compte courant' | 'Épargne' | 'Investissement';
  balance: number;
  cap?: number;            // legal ceiling for Livret A / LDDS — used to render capacity bar
  color: string;
}

interface MonthHistory {
  month: string;           // "mai 26" — short FR
  income: number;
  contrib: number;         // net worth gain that month
  spent: number;
  status: 'best' | 'good' | 'mid' | 'hard';
}
```

Persisted user state should also include monthly allocation snapshots (per-envelope `allocated` per month) so historical "mois récents" rows can be rebuilt accurately.

---

## 9. Copy / localisation

The app is **French-only** in this iteration. All strings live inline in the JSX — extract them into your localisation system. Notable French conventions to preserve:

- Number formatting: `toLocaleString('fr-FR')` — thin space thousands separator, comma decimal (e.g. `12 380 €`, `4,5 %`).
- Currency: `€` symbol after the number with a regular space, dim-coloured.
- Minus sign: `−` (U+2212), not ASCII `-`.
- Month names: short lowercase (mai, avr, mar, fév, jan, déc, nov, oct).
- "Dépassé", "OK", "Attention", "Bon", "Moyen", "Dur", "Record" — status vocabulary.
- "Allouer", "Continuer", "Valider l'allocation", "Modifier l'allocation" — primary CTAs.
- Names of envelope types: Fixes, Variables, Du mois, Permanentes, Épargne, Investissement.

---

## 10. Assets

No external image assets. Everything is:

- **SVG paths** drawn inline (nav icons, status-bar icons, alert icon, edit icon, plus, chevron, check, triangle marker).
- **Glyph icons** for envelope categories (`⌂ ◐ ◇ ◢ ◆ ◣ ○ ✈ ∞ ◈`) — these are placeholders. The implementation should swap them for a proper icon set (Lucide, Phosphor, SF Symbols, Material Symbols) with semantic names per envelope category.
- **Fonts** — DM Sans + Geist Mono via Google Fonts (URL in §4.2).

---

## 11. Accessibility / responsiveness notes

The prototype is sized for a single phone viewport — it does not include accessibility affordances. When implementing:

- Ensure all icon-only buttons (back, close, bell, FAB) have accessible labels.
- Progress bars need an accessible value announcement (`aria-valuenow` / equivalent).
- The numpad must be keyboard-accessible on platforms that support it; on mobile, ensure the native keyboard isn't summoned over the custom numpad.
- Colour ratios — the primary amber on dark passes AA. The dim text `#8ba4be` on `#090c12` passes AA for body but verify per-component.
- Tap targets — most are ≥ 44px; envelope rows are 60px+ tall. The "+ Ajouter", "Tout voir →" and timeframe chips in Patrimoine are visually small — bump their hit area to 44px.
- Support dynamic type — every `<Num>` and DM Sans usage should scale with the platform's font-size setting.

---

## 12. Files included

```
design/
├── Finance OS.html          # Entry — loads all JSX via Babel-standalone
├── data.jsx                 # Mock data (envelopes, transactions, accounts, history)
├── primitives.jsx           # FOS token object + Card / Progress / Pill / Num / SectionLabel / Sparkline / Donut + useCountUp
├── phone.jsx                # PhoneShell, BottomNav, FAB, ScreenHeader, ScreenSlide
├── app.jsx                  # Root: tab + overlay + sheet state machine
├── screen-home.jsx          # Accueil dashboard
├── screen-budget.jsx        # Budget list grouped by envelope type
├── screen-envelope.jsx      # Envelope detail overlay
├── screen-allocation.jsx    # 3-step allocation flow overlay
├── screen-patrimoine.jsx    # Net worth + donut + accounts
├── screen-history.jsx       # 12-month history + annual summary
└── screen-expense.jsx       # Bottom-sheet expense entry with numpad
```

To preview the design locally: open `design/Finance OS.html` in a modern browser (needs internet for Google Fonts + unpkg). It auto-scales to fit the viewport.

---

## 13. Recommended implementation order

1. Set up tokens (colours, type, radii, motion) as theme constants.
2. Build primitives (`Card`, `Progress`, `Pill`, `Num`, `SectionLabel`, `Sparkline`, `Donut`).
3. Build phone-chrome shell + bottom nav + FAB.
4. Wire navigation skeleton with empty screens.
5. Implement screens in this order — most reuse downstream:
   1. **Budget** (envelope row is the most-reused block)
   2. **Envelope detail** (validates the navigation model)
   3. **Accueil** (composes envelope row variants + sparkline + donut-adjacent layout)
   4. **Patrimoine** (introduces donut + account row)
   5. **Historique** (introduces month row + mini bars)
   6. **Allocation** (multi-step form)
   7. **Expense sheet** (custom numpad)
6. Hook up real data + persistence.
7. Add accessibility, localisation hooks, and motion polish (count-up, progress fill, sheet slide).

---

Questions about any specific screen, interaction, or token? Reference the matching source file in `design/` — the README and the source are designed to be read side by side.
