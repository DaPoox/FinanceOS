# Agent Workflow

How Claude behaves when given a task. Follow these steps in order.  
Never skip to execution without completing analysis and confirmation.

---

## Design Implementation Workflow

Used when building UI from HTML/JSX design files. Component by component — never full screens at once.

### Rules
- **Design source:** HTML/JSX files provided by the user. These are the source of truth for layout, spacing, and visual intent.
- **Scope:** One component per task. User controls what gets built next.
- **Colors:** Use existing tokens only. If the design uses a color not in `Color.kt`, map to the closest token and flag the substitution. No new raw hex values.
- **Animations & UX:** Implement fully — transitions, feedback, micro-interactions included, not deferred.
- **Font prerequisite:** `DM Sans` and `Geist Mono` `.ttf` files must be in `res/font/` before any typography renders.

### Per-Component Checklist
- [ ] Read the HTML/JSX for the component fully before writing any code
- [ ] Identify all colors used → map to tokens, flag any gaps
- [ ] Implement layout, spacing, and typography to match the design
- [ ] Add animations and interaction states (press, loading, error)
- [ ] Add `@Preview` with realistic data wrapped in `FinanceOSTheme`
- [ ] No business logic in the composable

---

## Tool Selection

Before reaching for a network tool, exhaust local options first.

- **git history / blame** → `git log --oneline` or `git blame` via Bash. Never MCP GitHub for reads — it's a network round-trip for data that's already local.
- **MCP Linear / MCP GitHub** → write operations only (update ticket status, create PR, add comment). For reading project state, use Bash git commands.
- **Symbol or pattern lookups** → `grep` / `find` via Bash before spawning an Explore agent.
- **Already-read files** → use conversation context. Don't re-fetch a file you read in the same session.

---

## Step 1 — Receive the Task

Read the task fully. If the request is ambiguous, ask **one** clarifying question before proceeding.  
Do not ask multiple questions at once.

---

## Step 2 — Analyse & Map Impact

Before planning, identify:

- Which **feature(s)** are concerned
- Which **modules** will be touched (`:domain`, `:data`, `:presentation`, `:core`, `:app`)
- Which **files** will be created, modified, or deleted
- Any **ripple effects** — if changing component X impacts Y and Z, list them explicitly
- Whether the task requires a decision not covered by existing docs

Output format:

```
## Analysis
- Feature: budget
- Modules to touch: :domain, :data, :presentation
- Files to create: BudgetRepository.kt, GetBudgetUseCase.kt, BudgetScreen.kt ...
- Files to modify: DataModule.kt, AppNavGraph.kt
- Ripple effects: none / [list if any]
- Open decisions: none / [list if any]
```

---

## Step 3 — Plan

Propose a step-by-step implementation plan.  
Each step is atomic — one file, one concern.  
Do not write code.

Rules:
- Each item = file path + one-line description of what changes and why
- No code snippets, no function signatures, no import lists, no pseudocode
- Max ~10 items for a normal ticket

Output format:

```
## Plan
1. :domain/budget/model/ — add Budget domain model
2. :domain/budget/repository/ — add BudgetRepository interface
3. :data/budget/local/ — add BudgetEntity + DAO
4. :data/budget/repository/ — implement BudgetRepositoryImpl
5. :domain/budget/usecase/ — add GetBudgetUseCase
6. :presentation/budget/ — add UiState, UiAction, UiEvent files
7. :presentation/budget/ — implement BudgetViewModel
8. :presentation/budget/ — add BudgetScreen (ScreenRoot + Screen)
9. :presentation/budget/navigation/ — add nav route + extension
10. :app/navigation/AppNavGraph.kt — wire new screen
11. DataModule.kt + PresentationModule.kt — register new bindings
```

**→ Stop here. Wait for confirmation before executing.**

---

## Step 4 — Execute

Execute the approved plan step by step.  
After each step, state what was done in one line before moving to the next.

After ALL steps are done and the build passes, stage every created/modified file — **this is mandatory, not optional:**

```bash
git add <file1> <file2> ...   # list files explicitly — never git add -A or git add .
```

Do NOT commit. Staging is the final action after each execution block — always run it before reporting done.

If during execution a decision arises that is not covered by the existing docs:
- Stop
- Describe the decision and the options
- Wait for input before continuing

---

## Step 5 — Verify

After execution:

- [ ] Project builds without errors (`./gradlew assembleDebug`)
- [ ] No business logic in Composables
- [ ] No Android dependencies in `:domain`
- [ ] All new files follow naming conventions from `compose.md`
- [ ] Koin modules updated
- [ ] Nav graph updated
- [ ] Ripple effects from Step 2 have been addressed
- [ ] No vertical `=` or `:` alignment in created/modified files
- [ ] Trailing commas on all multiline parameter and argument lists
- [ ] KDoc on all new public functions and classes

Report result:
```
## Done
- X files created, Y files modified
- Build: OK / FAILED (paste error)
- Deviations from plan: none / [describe]
```

**→ Stop here. Do not commit or push. Wait for explicit instruction.**

Commit and push are separate, explicit steps. Authorization to commit does NOT imply authorization to push. Authorization given for one task does NOT carry over to the next task. Each task requires its own explicit instruction.

---

## Confirmation Gates

| Moment                                      | Required |
|---------------------------------------------|----------|
| After Step 3 (plan ready)                   | ✅ Always |
| Impact touches 3+ modules                   | ✅ Always |
| Decision not covered by existing docs       | ✅ Always |
| Simple fix, 1 file, no architectural impact | ❌ Skip   |
| Claude wants to create a `Manager` or `Helper` class | ✅ Always |
| Extension created in the wrong module | ✅ Always |
| New extension file when an existing one could be extended | ✅ Always |
| Running build/test/lint commands (`./gradlew ...`) | ❌ Never  |
| Read-only bash commands (`ls`, `find`, `grep`, `curl`, `jar`, `python3` inspection, etc.) | ❌ Never  |
| Bash commands that write, delete, or modify files/state | ✅ Always |