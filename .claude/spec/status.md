# FinanceOS — Project Status

## What's done (committed & pushed)

| Ticket | What |
|--------|------|
| PRO-6 | Design system — Color, Theme, Typography (navy/gold, Material3) |
| PRO-7 to PRO-13 | Home screen components: NetWorthHero, InsightCard, BudgetMonthCard, EnvelopeMiniGrid, SparklineCard, RecentMonthsSection, HomeScreen |
| PRO-14 to PRO-16 | BudgetGlobalCard, EnvelopeRow, BudgetScreen + ViewModel |
| PRO-17 | ExpenseSheet (bottom sheet, numpad, confirmation animation) |
| PRO-18 | EnvelopeDetailScreen + ViewModel |
| PRO-19 | AllocationScreen (3-step wizard) + ViewModel |
| PRO-20 | PatrimoineScreen + ViewModel (DonutCard, sparkline, accounts) |
| PRO-21 | HistoryScreen + ViewModel (annual summary, month list, RECORD badge) |
| PRO-22 | Icons & Fonts — Lucide + DM Sans + Geist Mono |
| PRO-24 | Collapsable groups in StepAdjust (chevron animation, contextual buttons) |
| PRO-25 | Room entities (5) + IconMapper + deleted old envelopes package |
| PRO-26 | Room DAOs (5 interfaces) |
| PRO-27 | FinanceDatabase + DatabaseSeeder (full seed from data.jsx) |
| PRO-28 | Domain models (8) + repository interfaces (5) in `:domain` |
| PRO-29 | Use Cases — 7 Observe* use cases in `:domain` |
| PRO-30 | Repository implementations (5) + mappers (5) in `:data` |
| PRO-31 | All 6 ViewModels wired to real Room Flows (replaced all mock data) |
| PRO-32 | AddTransactionUseCase + BudgetViewModel.OnExpenseSave persists to DB |
| PRO-33 | AllocateMonthUseCase + CopyAllocationFromMonthUseCase + AllocationViewModel.OnNext persists month + allocations |

## What's left (no tickets yet)

**1. Loading states** — All 6 ViewModels emit empty `UiState()` before Room fills in, causing a flash of blank UI on every screen. Need `isLoading: Boolean` in UiState + skeleton/shimmer in the screens. `Result<T>` in `:core` is defined but unused.

**2. Error handling** — No `catch {}` in any ViewModel Flow chain. If a Room query throws, the Flow dies silently. No error state in any UiState.

**3. Add/edit envelope** — No screen. EnvelopeDao has `insert`, `update`, `softDelete` but nothing calls them from the UI. Was implemented in an early architecture but deleted in PRO-25.

**4. Add/edit account** — No screen. AccountDao has `insert` and `update` but nothing calls them from the UI.

**5. New month flow** — Nothing creates a new month row when the calendar rolls over. The app currently relies entirely on seeded month data. If "2026-06" doesn't exist in DB, the home screen shows nothing.

**6. Sparkline history** — DashboardViewModel and PatrimoineViewModel use a hardcoded 6-point series. No account balance history table exists in the DB schema.

**7. `SavedStateHandle` in `EnvelopeDetailViewModel`** — Currently receives `id: String` via Koin `parametersOf`. Process death on the detail screen loses the id.

**8. Database migrations** — `fallbackToDestructiveMigration()` in place. Any schema change nukes user data. Needs a migrations plan before real usage.

**9. Backup rules** — No `android:fullBackupContent` exclusions. Android Auto Backup can restore a stale Room DB after reinstall.

**10. ProGuard/R8 rules** — Release build will break. Room, Koin, Kotlin Serialization, and Lucide all need keep rules. No release signing config.

**11. Unit tests** — Architecture is fully testable (pure use cases, injected VM deps, no singletons) but coverage is zero.

---

## Short summary

The app renders all screens with real Room data and persists expenses + allocations correctly. It is a functional read/write demo. Items 1–6 are what separate "demo" from "daily driver." Items 7–11 are what separate "daily driver" from "shippable release."
