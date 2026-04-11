# Compose UI Patterns

## Core Principle

The UI is dumb. Composables render state and forward user actions — nothing more.  
Zero business logic. Zero data transformation. Minimal side effects.

---

## Screen Structure

Every screen is two composables in **one file** (`<Screen>Screen.kt`).

### ScreenRoot
Owns the ViewModel. Observes events. Passes state and `onAction` down.  
Never referenced directly by other screens — only by the nav graph.

### Screen
Receives only `state` and `onAction`. No ViewModel reference. Fully previewable.

```kotlin
// BudgetScreen.kt

@Composable
fun BudgetScreenRoot(
    onNavigateToDetail: (String) -> Unit,
    viewModel: BudgetViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BudgetUiEvent.NavigateToDetail -> onNavigateToDetail(event.id)
            is BudgetUiEvent.ShowSnackbar -> { /* show snackbar */ }
        }
    }

    BudgetScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun BudgetScreen(
    state: BudgetUiState,
    onAction: (BudgetUiAction) -> Unit
) { ... }

@Preview
@Composable
private fun BudgetScreenPreview() {
    FinanceOSTheme {
        BudgetScreen(
            state = BudgetUiState(
                totalBudget = 3000.0,
                spent = 1200.0
            ),
            onAction = {}
        )
    }
}
```

### ObserveAsEvents

Helper defined in `:core`. Collects a `Flow` as one-shot events,  
respecting the composition lifecycle.

```kotlin
// :core/extensions/FlowExtensions.kt
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle, key1, key2) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
```

---

## State Ownership

All state lives in the ViewModel. Never use `remember` or `rememberSaveable`  
for application state — use `StateFlow` and `collectAsStateWithLifecycle()`.

Exception: Compose-internal state the framework requires in composition.

```kotlin
// Acceptable — Compose-owned UI state
val lazyListState = rememberLazyListState()

// Reacting to Compose-owned state
val showScrollToTop by remember {
    derivedStateOf { lazyListState.firstVisibleItemIndex > 5 }
}
```

`derivedStateOf` only for Compose-internal state. If the derivation can happen  
in the ViewModel, it should.

---

## Stability & Recomposition

Strong skipping mode is enabled by default — no explicit opt-in needed.

Only annotate `@Stable` when the state class contains unstable fields  
(`List`, `Map`, `Set`, interfaces, abstract types).

```kotlin
// Needs @Stable — contains a List
@Stable
data class BudgetUiState(
    val expenses: List<ExpenseUi> = emptyList(),
    val isLoading: Boolean = false
)

// No annotation needed — all fields are stable primitives
data class AddExpenseUiState(
    val amount: String = "",
    val label: String = "",
    val isSaving: Boolean = false
)
```

---

## Uis

Screen-specific. Suffixed with `Ui`. Mappers live in the screen's `mapper/` package.  
A domain model can map to multiple Ui models. A Ui model can combine multiple domain models.

```kotlin
data class ExpenseUi(
    val id: String,
    val label: String,
    val formattedAmount: String,   // "42,00 €"
    val formattedDate: String      // "15 mar. 2026"
)

// :presentation/budget/mapper/ExpenseMapper.kt
fun Expense.toExpenseUi(): ExpenseUi = ExpenseUi(
    id = id,
    label = label,
    formattedAmount = amount.formatCurrency(),
    formattedDate = date.formatDisplay()
)
```

---

## Side Effects

If something can be handled by the ViewModel through a `UiAction`, do that instead.  
When a side effect is truly necessary (lifecycle APIs, etc.), extract it into  
a dedicated composable:

```kotlin
@Composable
fun ObserveLifecycle(onStart: () -> Unit, onStop: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_STOP -> onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
```

Do not use custom `CompositionLocal`s.

---

## Lazy Layouts

Add `key` when a unique identifier is available:

```kotlin
LazyColumn {
    items(
        items = state.expenses,
        key = { it.id }
    ) { expense ->
        ExpenseItem(
            expense = expense,
            onClick = { onAction(BudgetUiAction.OnExpenseClick(expense.id)) }
        )
    }
}
```

---

## Animations

Prefer approaches that animate below the recomposition layer.

```kotlin
// Good — animates without recomposition
val alpha by animateFloatAsState(if (state.isVisible) 1f else 0f)
Box(modifier = Modifier.graphicsLayer { this.alpha = alpha })

// Bad — causes recomposition on every frame
Box(modifier = Modifier.alpha(animatedAlpha))
```

Deferred state reads — pass as lambda to defer to layout/draw phase:

```kotlin
// Good
fun Modifier.animatedOffset(offsetProvider: () -> IntOffset) =
    offset { offsetProvider() }

// Bad
fun Modifier.animatedOffset(offset: IntOffset) =
    offset(x = offset.x.dp, y = offset.y.dp)
```

---

## Modifier Extensions

Plain extension functions or `Modifier.Node`-based factories.  
Never `@Composable` modifier extensions.

```kotlin
// Good
fun Modifier.roundedBackground(color: Color, radius: Dp) =
    background(color, RoundedCornerShape(radius))
```

---

## Design System

Lives in `:presentation/core/designsystem/`.  
Use slot APIs for design system components that need flexible content areas.  
Feature composables prefer typed parameters over slots.

```kotlin
@Composable
fun FinanceCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Card(modifier = modifier) {
        header()
        content()
    }
}
```

---

## TextField

Text input state lives in the ViewModel. Every keystroke dispatches a `UiAction`.

```kotlin
TextField(
    value = state.amount,
    onValueChange = { onAction(AddExpenseUiAction.OnAmountChange(it)) }
)
```

---

## Process Death

For screens with forms or critical user input, restore fields via `SavedStateHandle`:

```kotlin
class AddExpenseViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        AddExpenseUiState(
            amount = savedStateHandle["amount"] ?: "",
            label = savedStateHandle["label"] ?: ""
        )
    )

    fun onAction(action: AddExpenseUiAction) {
        when (action) {
            is AddExpenseUiAction.OnAmountChange -> {
                savedStateHandle["amount"] = action.amount
                _state.update { it.copy(amount = action.amount) }
            }
        }
    }
}
```

Only save what truly matters after process death — not the entire state.

---

## Previews

Every Screen composable has at least one `@Preview` with realistic data.  
Always wrap in the app theme.

```kotlin
@Preview
@Composable
private fun BudgetScreenPreview() {
    FinanceOSTheme {
        BudgetScreen(
            state = BudgetUiState(
                expenses = listOf(
                    ExpenseUi("1", "Loyer", "1 200,00 €", "1 avr. 2026"),
                    ExpenseUi("2", "Courses", "87,50 €", "3 avr. 2026")
                )
            ),
            onAction = {}
        )
    }
}
```

---

## Accessibility

Meaningful `contentDescription` on all interactive or informational elements.  
Always use string resources. `null` for purely decorative elements.

```kotlin
Icon(
    imageVector = Icons.Default.Delete,
    contentDescription = stringResource(R.string.cd_delete_expense)
)
```

---

## Naming Conventions

| Thing             | Convention           | Example                  |
|-------------------|----------------------|--------------------------|
| ViewModel         | `<Screen>ViewModel`  | `BudgetViewModel`        |
| UiState           | `<Screen>UiState`    | `BudgetUiState`          |
| UiAction          | `<Screen>UiAction`   | `BudgetUiAction`         |
| UiEvent           | `<Screen>UiEvent`    | `BudgetUiEvent`          |
| Root composable   | `<Screen>ScreenRoot` | `BudgetScreenRoot`       |
| Screen composable | `<Screen>Screen`     | `BudgetScreen`           |
| Ui           | `<Model>Ui`     | `ExpenseUi`         |

---

## Checklist: Adding a New Screen

- [ ] Create `<Screen>UiState`, `<Screen>UiAction`, `<Screen>UiEvent`
- [ ] Implement `<Screen>ViewModel`
- [ ] Create `<Screen>Screen.kt` with `ScreenRoot` + `Screen` in the same file
- [ ] Add `@Preview` with realistic data wrapped in `FinanceOSTheme`
- [ ] Map domain errors to `UiText` via extension functions
- [ ] Add `SavedStateHandle` for any form fields that must survive process death
- [ ] Add `@Stable` to UiState if it contains `List`, `Map`, or interface fields