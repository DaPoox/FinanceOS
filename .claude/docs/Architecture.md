# Architecture

## Overview

Clean Architecture — strict layer separation, unidirectional data flow.  
`:domain` is the center. `:data` and `:presentation` depend on it, never on each other.

```
:presentation  →  :domain  ←  :data
                     ↑
                   :core
```

---

## Presentation Layer

### UiState

A single `data class` per screen. Fields are nullable or have defaults.

```kotlin
data class BudgetUiState(
    val isLoading: Boolean = false,
    val totalBudget: Double = 0.0,
    val spent: Double = 0.0,
    val error: UiText? = null
)
```

### UiAction

What the UI sends to the ViewModel. One sealed class per screen.

```kotlin
sealed class BudgetAction {
    data class AddExpense(val amount: Double) : BudgetAction()
    data object Refresh : BudgetAction()
}
```

### ViewModel

- Exposes `StateFlow<UiState>` for state
- Exposes `Channel<UiEvent>.receiveAsFlow()` for one-shot side effects
- Receives `UiAction` via `onAction(action: XxxAction)`
- Delegates to UseCases for non-trivial or reusable logic
- Simple logic (field update, toggle) can live directly in the ViewModel

```kotlin
class BudgetViewModel(
    private val getBudgetUseCase: GetBudgetUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetUiState())
    val state: StateFlow<BudgetUiState> = _state.asStateFlow()

    private val _events = Channel<BudgetEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: BudgetAction) {
        when (action) {
            is BudgetAction.Refresh -> loadBudget()
        }
    }
}
```

### Side Effects (one-shot events)

Navigation, snackbars, toasts, BottomSheet triggers — anything that fires once  
and must not survive recomposition.

Use `Channel<UiEvent>` in the ViewModel, consumed in the Composable via `LaunchedEffect`.

```kotlin
// ViewModel
private val _events = Channel<BudgetEvent>()
val events = _events.receiveAsFlow()

// Composable
LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
        when (event) {
            is BudgetEvent.ShowAddExpenseSheet -> showBottomSheet()
            is BudgetEvent.NavigateToDetail -> navigator.navigate(...)
        }
    }
}
```

> Use `Channel` for single-collector one-shot events (default).  
> Use `SharedFlow` only when multiple collectors need the same event simultaneously.

### ViewModel Delegates

When a ViewModel grows too large, extract responsibilities into focused delegate classes.  
Each delegate owns one concern (e.g., validation, allocation logic, pagination).  
To be defined further when the need arises in practice.

### UiModels

UiModels are screen-specific. A single domain model can map to multiple UiModels  
depending on how the screen displays the data.  
A UiModel can also be built from multiple domain models combined.

```kotlin
// Dashboard shows a summary
data class DashboardBudgetUiModel(val remaining: String, val progressPercent: Float)

// Budget screen shows full detail
data class BudgetDetailUiModel(val total: Double, val spent: Double, val categories: List<...>)
```

Mappers live in the package of the screen that consumes them — not centralized.

```
:presentation
├── dashboard/
│   ├── DashboardScreen.kt
│   ├── DashboardUiState.kt
│   └── mapper/
│       └── BudgetDashboardMapper.kt   ← Budget → DashboardBudgetUiModel
├── budget/
│   ├── BudgetScreen.kt
│   ├── BudgetUiState.kt
│   └── mapper/
│       └── BudgetDetailMapper.kt      ← Budget → BudgetDetailUiModel
```

---

## Domain Layer

### UseCases
Always create UseCases for API calls, or any call that goes via repository. 
Do not call repository directly in the ViewModel.

`operator fun invoke()` as entry point. One class = one responsibility. Use Suspend if necessary.

```kotlin
class ComputeAllocationUseCase(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(paycheck: Double): Result<Allocation> { ... }
}
```

Naming: verb + noun — `GetBudgetUseCase`, `AddExpenseUseCase`, `ComputeAllocationUseCase`.

### Repository Interfaces

Defined in `:domain`. Implemented in `:data`. Domain never knows the implementation.

```kotlin
interface BudgetRepository {
    suspend fun getBudget(): Result<Budget>
    suspend fun saveBudget(budget: Budget): Result<Unit>
}
```

### Domain Models

Pure Kotlin data classes. No Room annotations, no JSON annotations, no Android imports.

---

## Data Layer

### Repository Implementations

Fetch and persist only. No business logic.  
Map entities to domain models via `toDomain()` before returning.

```kotlin
class BudgetRepositoryImpl(
    private val dao: BudgetDao
) : BudgetRepository {
    override suspend fun getBudget(): Result<Budget> = try {
        Result.Success(dao.getBudget().toDomain())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

### Mappers (Entity ↔ Domain)

Extension functions. Live in `:data`, in the package of the feature they belong to.  
Named by context, not by type.

```kotlin
// BudgetMappers.kt
fun BudgetEntity.toDomain(): Budget = Budget(id = id, total = total)
fun Budget.toEntity(): BudgetEntity = BudgetEntity(id = id, total = total)
```

---

## Core

### Result\<T\>

Industry standard for exhaustive state handling. Includes `Loading` — which  
`runCatching` does not support natively.

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
```

### UiText

Abstracts string representation across layers.  
The ViewModel never hardcodes strings or depends on Android Context.

```kotlin
sealed class UiText {
    // For string resources — localized, static
    data class Resource(@StringRes val id: Int) : UiText()

    // For dynamic content — API messages, computed values, exception messages
    data class Dynamic(val value: String) : UiText()
}

// Usage in ViewModel
UiText.Resource(R.string.error_budget_not_found)
UiText.Dynamic("Montant maximum : ${account.cap}€")
UiText.Dynamic(e.message ?: "Unknown error")

// Resolved in Composable
fun UiText.asString(context: Context): String = when (this) {
    is UiText.Resource -> context.getString(id)
    is UiText.Dynamic -> value
}
```

### Extensions

Grouped by context of usage — not by type or feature.  
The file name reflects when and why these functions are used together.

```
:core
└── extensions/
    ├── DateExtensions.kt
    ├── CurrencyExtensions.kt
    └── FlowExtensions.kt
```

---

## Dependency Injection (Koin)

One `Module.kt` per Gradle module. `:app` assembles everything via `startKoin`.

```
:data         → DataModule.kt         (repositories, DAOs, Room DB)
:presentation → PresentationModule.kt (ViewModels)
:domain       → no Koin declarations  (pure interfaces)
:app          → startKoin { modules(dataModule, presentationModule) }
```

---

## Data Flow

```
User interaction
      ↓
UiAction → ViewModel.onAction()
      ↓
UseCase.invoke()  ←  only if non-trivial
      ↓
Repository.fetch() → Room DAO
      ↓
Entity → toDomain() → Domain Model
      ↓
Result<T> → ViewModel maps to UiModel → updates UiState
      ↓
StateFlow → Composable recomposes
```

---

## Rules

- `:domain` has zero Android dependencies — pure Kotlin only
- `:data` repositories fetch and persist only — no business logic
- Composables contain zero logic — all logic lives in ViewModel or UseCase
- `UiState` is always a `data class` with defaults
- One-shot events (navigation, snackbar, BottomSheet) go through `Channel`
- UseCases are created for non-trivial or shared logic — not for every repo call
- Domain models carry no Room or serialization annotations
- UiModels are screen-specific — mappers live in the screen package that owns them
- Extensions are grouped by context of usage, not by type or feature
