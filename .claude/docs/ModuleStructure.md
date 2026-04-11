# Module Structure

## Philosophy

Layer-based modularization — one Gradle module per layer.  
Features are packages inside each module, not separate modules.  
Code moves to `:core` only when shared across 2+ modules and has no Android/Compose dependency.

---

## Modules

### `:app`
Entry point. Assembles everything.
- Koin initialization
- NavGraph root
- Application class

### `:domain`
Pure Kotlin. Zero Android dependencies.
- Domain models
- Repository interfaces
- UseCases (`operator fun invoke()`)

### `:data`
Android, no Compose.
- Repository implementations
- Room database, DAOs, entities
- Mappers (entity ↔ domain model)

### `:presentation`
Android + Compose.
- ViewModels
- Composable screens
- UiState, UiEvent
- Design system (theme, colors, typography, shared components)

### `:core`
No Android. No Compose.
- `Result<T>` sealed class
- `UiText` (string/error abstraction)
- Kotlin extensions shared across modules

---

## Dependency Rules

| Module          | May depend on               |
|-----------------|-----------------------------|
| `:app`          | all modules                 |
| `:presentation` | `:domain` · `:core`         |
| `:data`         | `:domain` · `:core`         |
| `:domain`       | `:core` only                |
| `:core`         | nothing                     |

---

## Package Structure

```
:domain
├── budget/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── expenses/
└── savings/

:data
├── budget/
│   ├── local/
│   ├── mapper/
│   └── repository/
├── expenses/
└── savings/

:presentation
├── budget/
│   ├── BudgetScreen.kt
│   ├── BudgetViewModel.kt
│   ├── BudgetUiState.kt
│   └── BudgetEvent.kt
├── expenses/
├── savings/
└── core/
    └── designsystem/
        ├── Theme.kt
        ├── Color.kt
        └── Typography.kt
```

---

## Convention Plugins (`:build-logic`)

| Plugin                | Used by                              |
|-----------------------|--------------------------------------|
| `android-application` | `:app`                               |
| `android-library`     | `:data` · `:presentation` · `:core`  |
| `domain-module`       | `:domain`                            |
| `compose`             | `:presentation`                      |
| `koin`                | `:app` · `:data` · `:presentation`   |
| `room`                | `:data`                              |

---

## Rules

- `:domain` has zero Android dependencies — pure Kotlin only
- `:core` has zero Android and Compose dependencies
- Features never depend on each other
- `:data` repositories fetch and persist only — no business logic
- `:presentation` ViewModels delegate all logic to UseCases
- `:app` depends on everything — nothing depends on `:app`

---

## Checklist: Adding a New Feature

- [ ] `:domain` — add `model/`, repository interface, UseCases
- [ ] `:data` — add Room DAO, repository implementation, mappers
- [ ] `:presentation` — add ViewModel, Screen composable, UiState, UiEvent
- [ ] Wire Koin — update `Module.kt` in each concerned module
- [ ] Add navigation route in `:app` NavGraph