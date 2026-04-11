# Dependency Injection (Koin)

## Principles

- One Koin module per Gradle module — only if there are dependencies to provide
- Modules assembled in `:app` only — never in the modules themselves
- ViewModels injected via `koinViewModel()` in ScreenRoot composables only

---

## Module Definitions

Prefer constructor-reference overloads (`singleOf`, `viewModelOf`, `factoryOf`).  
Fall back to lambda overloads (`single { }`, `viewModel { }`) only when a factory method,  
named qualifier, or post-construction setup is needed.

### `:data` module

```kotlin
// :data/DataModule.kt
val dataModule = module {
    singleOf(::BudgetRepositoryImpl) { bind<BudgetRepository>() }
    singleOf(::ExpenseRepositoryImpl) { bind<ExpenseRepository>() }
    singleOf(::SavingsRepositoryImpl) { bind<SavingsRepository>() }

    // Lambda overload — factory method, not a constructor
    single { FinanceDatabase.create(get()) }
    single { get<FinanceDatabase>().budgetDao() }
    single { get<FinanceDatabase>().expenseDao() }
}
```

### `:presentation` module

```kotlin
// :presentation/PresentationModule.kt
val presentationModule = module {
    viewModelOf(::BudgetViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::ExpensesViewModel)
    viewModelOf(::SavingsViewModel)
}
```

---

## Assembly in `:app`

```kotlin
// :app/FinanceOSApp.kt
class FinanceOSApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FinanceOSApp)
            modules(
                dataModule,
                presentationModule
            )
        }
    }
}
```

---

## Injecting in Composables

Always use `koinViewModel()` in ScreenRoot composables. Never pass ViewModels down the tree.

```kotlin
@Composable
fun BudgetScreenRoot(
    onNavigateToDetail: (String) -> Unit,
    viewModel: BudgetViewModel = koinViewModel()
) { ... }
```

---

## Scoping Rules

| Scope     | Preferred form                          | Fallback          | When to use                                      |
|-----------|-----------------------------------------|-------------------|--------------------------------------------------|
| Singleton | `singleOf(::Impl) { bind<Interface>() }`| `single { ... }`  | One instance for app lifetime (repos, DB, DAOs)  |
| ViewModel | `viewModelOf(::MyViewModel)`            | `viewModel { ... }`| Scoped to ViewModel lifecycle                   |
| Factory   | `factoryOf(::Impl)`                     | `factory { ... }` | New instance on every injection (rare)           |

---

## Naming Conventions

| Thing       | Convention       | Example                |
|-------------|------------------|------------------------|
| Koin module | `<layer>Module`  | `dataModule`, `presentationModule` |

---

## Checklist: Adding DI for a New Feature

- [ ] Add repository binding in `DataModule.kt`
- [ ] Add ViewModel in `PresentationModule.kt`
- [ ] No new module files needed — one per Gradle module only