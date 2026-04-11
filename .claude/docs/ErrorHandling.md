# Error Handling

## Result Wrapper (`:core`)

Typed Result with explicit error type. Forces error handling at every layer.

```kotlin
interface Error

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : com.daprox.financeos.core.Error>(val error: E) : Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>
```

> This replaces the simpler `Result<T>` with `Loading` — Loading state is handled  
> via `isLoading: Boolean` in `UiState`, not in the Result wrapper.

---

## Extension Helpers (`:core`)

Chainable. Live alongside the `Result` definition.

```kotlin
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> =
    when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }

inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> =
    also { if (this is Result.Success) action(data) }

inline fun <T, E : Error> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E> =
    also { if (this is Result.Error) action(error) }

fun <T, E : Error> Result<T, E>.asEmptyResult(): EmptyResult<E> = map { }
```

Usage:
```kotlin
repository.saveBudget(budget)
    .onSuccess { /* update UI */ }
    .onFailure { /* show error */ }
```

---

## Error Types

### Shared — `DataError` (`:core`)

Local storage errors only. No network — FinanceOS is offline-first.

```kotlin
sealed interface DataError : Error {
    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN
    }
}
```

### Feature-specific errors

Each feature defines its own error types by implementing `Error`.

```kotlin
// :domain/budget/model/BudgetError.kt
enum class BudgetError : Error {
    INVALID_AMOUNT,
    BUDGET_NOT_FOUND,
    ALLOCATION_EXCEEDS_TOTAL
}

// Usage
fun validateAllocation(amount: Double, total: Double): EmptyResult<BudgetError> =
    if (amount > total) Result.Error(BudgetError.ALLOCATION_EXCEEDS_TOTAL)
    else Result.Success(Unit)
```

Always a single error per Result — never a list.

---

## Exception Handling Philosophy

Never throw exceptions for expected failures — always return `Result.Error`.  
Catch exceptions at the layer that owns them.

| Exception origin | Catch in     | Map to                        |
|------------------|--------------|-------------------------------|
| Room / SQLite    | `:data`      | `DataError.Local`             |
| Business logic   | `:domain`    | Feature-specific `Error` enum |
| Input validation | `:domain`    | Feature-specific `Error` enum |

Upper layers never see raw exceptions for expected failures.

```kotlin
// :data
class BudgetRepositoryImpl(private val dao: BudgetDao) : BudgetRepository {
    override suspend fun getBudget(): Result<Budget, DataError.Local> = try {
        Result.Success(dao.getBudget().toDomain())
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }
}
```

---

## Mapping Errors to UiText

Every user-facing error needs a `.toUiText()` extension.

- Feature-specific errors → in the feature's package in `:presentation`
- Shared errors (`DataError`) → in `:core`

```kotlin
// :core/extensions/DataErrorExtensions.kt
fun DataError.toUiText(): UiText = when (this) {
    DataError.Local.DISK_FULL -> UiText.Resource(R.string.error_disk_full)
    DataError.Local.NOT_FOUND -> UiText.Resource(R.string.error_not_found)
    DataError.Local.UNKNOWN   -> UiText.Resource(R.string.error_unknown)
}

// :presentation/budget/mapper/BudgetErrorMapper.kt
fun BudgetError.toUiText(): UiText = when (this) {
    BudgetError.INVALID_AMOUNT          -> UiText.Resource(R.string.error_invalid_amount)
    BudgetError.BUDGET_NOT_FOUND        -> UiText.Resource(R.string.error_budget_not_found)
    BudgetError.ALLOCATION_EXCEEDS_TOTAL -> UiText.Resource(R.string.error_allocation_exceeded)
}
```

Errors that are purely internal and never shown to the user do not need `.toUiText()`.

---

## When to Use What

| Scenario               | Error type              | Return type                          |
|------------------------|-------------------------|--------------------------------------|
| Room / local DB        | `DataError.Local`       | `Result<Budget, DataError.Local>`    |
| Repository multi-source| `DataError`             | `Result<Budget, DataError>`          |
| Domain validation      | Feature `Error` enum    | `EmptyResult<BudgetError>`           |
| Input validation       | Feature `Error` enum    | `EmptyResult<ValidationError>`       |

---

## Rules

- `Result` is used across all layers — not just data
- Never use `throw` for expected failures — return `Result.Error`
- Loading state lives in `UiState.isLoading` — not in `Result`
- Always map errors to `UiText` before surfacing them in the UI
- One error per Result — never a list