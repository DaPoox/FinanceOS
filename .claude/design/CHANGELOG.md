Ll# Finance OS — Changelog

Mobile personal-finance prototype (412 × 892, dark mode, FR). Envelope budgeting,
net-worth tracking, monthly allocation, manual expense capture.

## Entry points
- **`Finance OS.html`** — the live, clickable prototype (loads `app.jsx` + screens).
- **`Finance OS - New Designs.html`** — a review canvas of approved mockups
  (Briefs 0–3), laid out side by side. These are *static designs*, not wired
  into the live app unless noted below.

## Brief status
| Brief | What | State |
|---|---|---|
| 0 · Empty states | Fresh-install / no-data screens | **Mockup only** |
| 1 · Envelope form | Create/edit envelope | **Mockup only** |
| 2 · Account form | Create/edit account | **Mockup only** |
| 3 · New-month flow | Unallocated banner + adaptive allocation | **Live** ✅ |

---

## 2026-05-30 — Brief 3 wired into the live app
Integrated the new-month / start-of-month workflow into `Finance OS.html`.

- **New-month detection** (`app.jsx`): added a persisted `monthAllocated` flag
  (localStorage `fos_month_allocated`). Defaults to **false** so the app opens on
  the unallocated state — the actual first-open / start-of-month moment.
- **Home swap**: when unallocated, Home renders `HomeWithUnallocatedBanner`
  (Brief 3) instead of the normal dashboard; once allocated it shows `HomeScreen`,
  relabelled to **Juin 2026**.
- **Adaptive allocation** (`screen-allocation.jsx`): `AllocationScreen` now takes
  `monthLabel`, `previousMonth`, `hasTemplate`. With history it runs **3 steps**
  (revenu → template → ajuste); first-ever month falls back to **2 steps**. New
  `StepTemplate` (mois précédent / moyenne 3 mois / modèle vierge) prefills the
  envelope amounts on selection.
- **Completion** flips the month to allocated and reveals the dashboard.
- **Demo reset**: a discreet "démo" pill (bottom-left) returns to the unallocated
  state so the flow can be replayed. Prototype-only affordance.
- `HomeScreen` month label made parametric (`monthLabel` prop).
- Loaded `screen-new-month.jsx` into `Finance OS.html`.

### Known gap (next)
Empty state (Brief 0) and new-month (Brief 3) currently share one trigger
("month not allocated"), so a **true first launch wrongly shows Brief 3**
(net worth + a "Mai clôturé" recap that never happened). Needs a second
`hasData` flag layered above `monthAllocated`:
- no data → `EmptyHomeScreen` (Brief 0)
- data but unallocated → `HomeWithUnallocatedBanner` (Brief 3)
- allocated → `HomeScreen`

Also still un-integrated: Brief 1 (envelope form), Brief 2 (account form).
