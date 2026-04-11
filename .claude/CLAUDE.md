# FinanceOS — CLAUDE.md

Personal finance app. Budget tracking, expenses, savings, ETF investments.  
Solo dev. Manual data entry only. No bank API.

**Package:** `com.daprox.financeos`  
**Min SDK:** 27 · **Target SDK:** 36

---

## Stack

Kotlin · Jetpack Compose · Room · Koin · Clean Architecture  
No XML layouts. No LiveData. No Hilt.

---

## Modules

```
:app            ← entry point, Koin init, NavGraph
:domain         ← pure Kotlin, UseCases, repository interfaces, domain models
:data           ← Room, DAOs, repository implementations, mappers
:presentation   ← ViewModels, Composables, UiState, UiAction, design system
:core           ← Result<T>, UiText, extensions (no Android, no Compose)
:build-logic    ← Gradle convention plugins
```

→ Full rules: @.claude/docs/module-structure.md

---

## Architecture

Unidirectional data flow. Layer boundaries are strict.

- `UiState` → `data class` with defaults
- `UiAction` → sealed class, sent from UI to ViewModel via `onAction()`
- Side effects → `Channel<UiEvent>.receiveAsFlow()` (navigation, snackbar, BottomSheet)
- `Result<D, E>` → typed sealed interface in `:core` with `Success` and `Error`. Loading state lives in `UiState.isLoading`
- `Ui models` → screen-specific, mappers live in the screen package
- Extensions → grouped by context of usage, not by type or feature

→ Full rules: @.claude/docs/architecture.md

---

## Build Commands

```bash
./gradlew assembleDebug                                          # build
./gradlew test                                                   # all unit tests
./gradlew :module:test --tests "com.daprox.ClassName.method"    # single test
./gradlew lint                                                   # lint
```

---

## Code Style

Comment all classes, functions, and non-obvious logic blocks. Comments explain intent, not restate the code — enough to navigate the codebase at a glance.

---

## Absolute Rules

- Zero Android dependencies in `:domain`
- Zero Compose dependencies in `:core`
- No business logic in Composables
- No business logic in `:data` repositories — fetch and persist only
- No LiveData anywhere — StateFlow only
- Ui models are screen-specific — never shared across screens
- `:app` depends on everything — nothing depends on `:app`
- One-shot events go through `Channel`, not `StateFlow`
- Inject `CoroutineDispatcher` only if the class is unit-tested AND dispatches to a non-main dispatcher

---

## Docs

- Module structure: @.claude/docs/ModuleStructure.md
- Architecture patterns: @.claude/docs/Architecture.md
- Compose UI patterns: @.claude/docs/Compose.md
- Navigation: @.claude/docs/Navigation.md
- Error handling: @.claude/docs/ErrorHandling.md
- Dependency injection: @.claude/docs/DI.md
- Agent workflow: @.claude/docs/workflow.md