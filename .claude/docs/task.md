# Current Task: Rebalance Envelopes Screen

## What to build
The envelope rebalancing screen — a full-screen modal (not a bottom sheet) that opens when the user taps "Modifier" on the Envelopes screen. This is where the user manually adjusts how their monthly income is split across budget categories.

## Spec reference
`.claude/specs/rebalance-envelopes.html` — use this as the visual and structural reference.

## Design reference
`.claude/docs/design-system.md` — Emerald Ledger design system. Dark mode only.

## Screen description
**Header:** Fixed top bar with a close (✕) button on the left, "REBALANCE ENVELOPES" title centered in primary green, checkmark (✓) confirm button on the right.

**Allocation visualizer:** Shows total allocation percentage (e.g. 112% in red if over, 89% in green if under). A segmented horizontal bar shows each envelope as a colored segment. Reacts in real time as the user moves sliders.

**Warning banner:** Appears only when total > 100%. Red card with warning icon and message explaining the overage amount.

**Envelope list:** Each envelope card shows name, category label, current percentage (colored), euro amount, and a slider. Some envelopes also have −/+ stepper buttons flanking the slider. The over-budget envelope highlights with a tertiary border and shows a "REBALANCE SUGGESTION / Apply Fix" row.

## Behavior
- Sliders and steppers are fully manual — no auto-redistribution
- Total percentage updates in real time
- Status: green when = 100%, blue when < 100%, red when > 100%
- Warning banner appears/disappears dynamically
- Confirm button (✓) saves and closes the screen
- Close button (✕) discards changes

## Acceptance criteria
- [ ] Full-screen modal with fixed header and bottom nav
- [ ] Segmented bar updates in real time
- [ ] Warning card visible only when over 100%
- [ ] Each envelope has slider + optional stepper
- [ ] Over-budget envelope has distinct visual treatment
- [ ] Confirm saves, close discards

## Out of scope
- Auto-redistribution logic
- Preset saving ("Mois serré", etc.) — post-MVP
- Animations — keep it functional first