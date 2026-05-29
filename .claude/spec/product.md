# FinanceOS — Product & Business Rules

> **Keep this document up to date.** Whenever a feature is added, changed, or removed, update the relevant section here. This is the single source of truth for product behaviour.

---

## What is FinanceOS?

A personal finance app for manual budget tracking. No bank sync, no automatic imports. The user enters everything themselves. The goal is full awareness of where money goes each month.

---

## Core concepts

### Envelope
A named budget bucket. Every euro of monthly income is distributed into envelopes. Spending is recorded against an envelope.

**Types:**
- **Variable** — recurring monthly spend with a variable amount (groceries, restaurants). Resets each month.
- **Monthly** ("Du mois") — a one-off expense for the current month (a trip, a purchase). Resets each month.
- **Fixed** ("Fixes") — fixed charges: rent, subscriptions, insurance. Amount doesn't change. Displayed separately from the rest of the budget.
- **Permanent** — long-running goal that accumulates over time (emergency fund). Amount carries forward.
- **Savings** — savings envelope. Tracks contributions to a savings account.
- **Investment** — investment envelope. Tracks contributions to investments (ETF, etc.).

An envelope has a name, an icon, a type, and can be archived when no longer needed.

### Month
A budget period. Identified by `YYYY-MM`. Has an income figure and a list of envelope allocations. A month is either **allocated** (the user has distributed income across envelopes) or **not yet allocated**.

### Allocation
The act of distributing monthly income across envelopes. Done via the Allocation screen (3-step wizard). Until a month is allocated, no budget data exists for that month.

### Transaction / Expense
A spending event recorded against an envelope and a month. Amount + optional note. Manual entry only.

### Account (Patrimoine)
A financial account tracked for net worth purposes: bank account, savings account, investment portfolio, etc. Has a name, a type (checking, savings, investment, other), an icon, and a balance. Not linked to budget envelopes — patrimoine is tracked separately.

---

## Screens

### Dashboard (Home)
Overview of the user's financial situation.

- **Empty state** (no data): "Bienvenue sur Finance OS. Commence par allouer ton premier mois pour suivre ton budget." CTA → Allocation.
- **Loaded state**: net worth hero card, insight card, budget month card, envelope mini-grid, sparkline (patrimoine evolution), recent months.
- **Budget month card**: shows current month spend vs allocated. If the month is not yet allocated, shows the `UnallocatedMonthBanner` — a gold dashed CTA to trigger allocation.

### Budget
List of envelopes and their spend for the current month.

- **Empty state**: "Aucune enveloppe. Alloue ton premier mois pour créer tes enveloppes de budget." CTA → Allocation.
- **Loaded state**: global summary card (income, total spent, total allocated), fixed charges summary (collapsible), envelope rows grouped by type.
- FAB to add a new expense.
- "Allouer" button in header to go back to the Allocation screen.

### Fixes (Fixed charges detail)
Dedicated screen for fixed charges only. Shows total allocated vs spent, and a row per fixed envelope with status (OK / over budget).
Accessed from the collapsible `FixesSummaryCard` in Budget.

### Allocation (3-step wizard)
The user distributes monthly income across envelopes.

- **Step 1 — Income**: enter the month's income.
- **Step 2 — Template**: optionally copy allocations from the previous month.
- **Step 3 — Adjust**: per-envelope amount fields, grouped by type. Swipe to remove an envelope (with undo snackbar). "+" button per group to add a new envelope directly (inline bottom sheet for Variable/Monthly, redirect message for other types).

### Envelope Detail
Shows all transactions for a given envelope in the current month. Header with spend progress. 3-dot menu with:
- **Renommer** → navigates to Envelope Form (edit mode).
- **Archiver** → confirm dialog, then archives the envelope (hidden from future months).

### Envelope Form
Create or edit an envelope. Fields: name, type, icon, color. All types available. Accessible from the Envelopes tab (not yet built as a standalone tab) and from the EnvelopeDetail 3-dot menu (edit mode).

### Patrimoine
Net worth screen. Lists all accounts with their balances. Total net worth at the top.

- **Empty state**: "Aucun compte. Ajoute ton premier compte pour suivre ton patrimoine." CTA → Account Form.
- FAB to add a new account.
- Tap an account → Account Form (edit mode).

### Account Form
Create or edit a financial account. Fields: name, type (checking / savings / investment / other), icon, balance.

### History
List of past months with their income, status, and contribution to net worth.

- **Empty state**: "Aucun historique. Tes mois apparaîtront ici une fois que tu auras alloué ton premier mois."

### Expense Sheet (bottom sheet)
Quick expense entry. Opened via the FAB on Budget and Dashboard. Fields: amount, envelope (chip selector), note (optional).

---

## Key business rules

### New month
- **Never automatic.** A new month is always triggered by the user.
- When the calendar advances, the app does not create anything. The `UnallocatedMonthBanner` on Dashboard is the entry point — the user taps it to start allocation for the new month.
- A notification (future) can remind the user that a new month is available, but the action is always manual.
- Until the user allocates the new month, all screens continue showing the previous month's data.

### Allocation
- Income must be > 0 and there must be at least one envelope to confirm allocation.
- Allocation overwrites any previous allocation for that month (re-allocating is allowed).
- The "copy from previous month" step (Step 2) is optional.

### Fixed charges
- Fixed envelopes are treated differently from variable/monthly ones. They appear in a dedicated `FixesSummaryCard` (collapsible) at the top of the Budget list, not mixed into the regular groups.
- Fixed charges have two statuses: OK and Over.

### Envelope types and new-envelope flow
- **Variable** and **Monthly** envelopes can be created inline from the Allocation screen (Step 3) via the `NewEnvelopeSheet` bottom sheet.
- **Fixed, Permanent, Savings, Investment** envelopes must be created via the full Envelope Form screen — they require additional configuration. The inline sheet shows a redirect message for these types.

### Archiving
- Archived envelopes are hidden from new months and from the active envelope list. They are not deleted. Historical transactions remain intact.

### Patrimoine vs Budget
- Accounts (patrimoine) and envelopes (budget) are separate. An account balance is not computed from transactions — the user sets it manually.

---

## States every screen must handle

| State | Description |
|---|---|
| Loading | Data is being fetched. Show skeletons. |
| Error | Fetch failed. Show error state with retry. |
| Empty | No data exists yet. Show empty state with contextual message and CTA if applicable. |
| Content | Normal populated state. |
