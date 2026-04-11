# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

→ Full project instructions: [.claude/CLAUDE.md](.claude/CLAUDE.md)

---

## Project Status

Early setup phase. Only `:app` module exists today. The multi-module structure below is the **target architecture** — modules are created incrementally as features are built.

---

## Target Module Structure

```
:app            ← entry point, Koin init, NavGraph
:domain         ← pure Kotlin, UseCases, repository interfaces, domain models
:data           ← Room, DAOs, repository implementations, mappers
:presentation   ← ViewModels, Composables, UiState, UiAction, design system
:core           ← Result<T>, UiText, extensions (no Android, no Compose)
:build-logic    ← Gradle convention plugins
```

**Dependency direction:** `:presentation` → `:domain` ← `:data`, all → `:core`, only `:app` depends on everything.

---

## Stack

Kotlin · Jetpack Compose · Room · Koin · Clean Architecture  
**Package:** `com.daprox.financeos` · **minSdk:** 27 · **targetSdk:** 36  
No XML layouts. No LiveData. No Hilt.

---

## Build Commands

```bash
./gradlew assembleDebug                                          # build
./gradlew test                                                   # all unit tests
./gradlew :module:test --tests "com.daprox.ClassName.method"    # single test
./gradlew lint                                                   # lint
```

---

## Mandatory Workflow (for any non-trivial task)

Before writing code, output:

```
## Analysis
- Feature: <name>
- Modules to touch: <list>
- Files to create/modify/delete: <list>
- Ripple effects: <none or list>
- Open decisions: <none or list>

## Plan
1. <atomic step — one file, one concern>
2. ...
```

**Stop and wait for confirmation before executing** when the plan touches 3+ modules, or involves an architectural decision not covered by the docs.

Skip the gate for simple single-file fixes with no architectural impact.

---

## Architecture Patterns

- `UiState` → `data class` with defaults; `isLoading: Boolean` for loading state
- `UiAction` → sealed class; UI sends to ViewModel via `onAction()`
- Side effects → `Channel<UiEvent>.receiveAsFlow()` (navigation, snackbar, BottomSheet)
- `Result<T>` → sealed interface in `:core` with `Success` and `Error` variants
- UiModels → screen-specific; mappers live in the screen package
- UseCases → only for non-trivial or reused logic; simple 1:1 repo calls go direct from ViewModel
- Koin → `singleOf`/`viewModelOf`/`factoryOf` preferred; assembled in `:app` only

---

## Absolute Rules

- Zero Android dependencies in `:domain`
- Zero Compose dependencies in `:core`
- No business logic in Composables
- No business logic in `:data` repositories — fetch and persist only
- No LiveData anywhere — StateFlow only
- UiModels are screen-specific — never shared across screens
- `:app` depends on everything — nothing depends on `:app`
- One-shot events go through `Channel`, not `StateFlow`
- Inject `CoroutineDispatcher` only if the class is unit-tested AND dispatches to a non-main dispatcher

---

## Docs

- Architecture patterns: [.claude/docs/Architecture.md](.claude/docs/Architecture.md)
- Module structure: [.claude/docs/ModuleStructure.md](.claude/docs/ModuleStructure.md)
- Compose UI patterns: [.claude/docs/Compose.md](.claude/docs/Compose.md)
- Navigation: [.claude/docs/Navigation.md](.claude/docs/Navigation.md)
- Error handling: [.claude/docs/ErrorHandling.md](.claude/docs/ErrorHandling.md)
- Dependency injection: [.claude/docs/DI.md](.claude/docs/DI.md)
- Agent workflow: [.claude/docs/workflow.md](.claude/docs/workflow.md)
