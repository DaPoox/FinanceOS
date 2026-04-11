# Navigation

## Principles

- Type-safe navigation with `@Serializable` route objects (KotlinX Serialization)
- One nav graph per feature, defined as a `NavGraphBuilder` extension in `:presentation`
- All feature nav graphs assembled in `:app`
- Intra-feature navigation uses `NavController` passed into the feature graph
- Cross-feature navigation uses lambda callbacks — never direct route imports between features

---

## Route Objects

Defined in `:presentation`, in the feature package.

```kotlin
// :presentation/budget/navigation/BudgetRoutes.kt
@Serializable data object BudgetRoute
@Serializable data class BudgetDetailRoute(val budgetId: String)
```

- `data object` for screens with no parameters
- `data class` for screens with arguments
- Never pass complex objects — pass IDs, load data in the destination ViewModel

---

## Feature Nav Graph

Each feature exposes a `NavGraphBuilder` extension function in `:presentation`.

```kotlin
// :presentation/budget/navigation/BudgetNavGraph.kt
fun NavGraphBuilder.budgetGraph(
    navController: NavController,
    onNavigateToDashboard: () -> Unit          // cross-feature callback
) {
    navigation<BudgetRoute>(startDestination = BudgetRoute) {
        composable<BudgetRoute> {
            BudgetScreenRoot(
                onNavigateToDetail = { id ->
                    navController.navigate(BudgetDetailRoute(id))
                }
            )
        }
        composable<BudgetDetailRoute> { backStackEntry ->
            val route: BudgetDetailRoute = backStackEntry.toRoute()
            BudgetDetailScreenRoot(
                budgetId = route.budgetId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToDashboard = onNavigateToDashboard
            )
        }
    }
}
```

---

## Wiring in `:app`

All graphs assembled in one `NavHost`. Cross-feature navigation wired here via lambdas.

```kotlin
// :app/navigation/AppNavGraph.kt
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BudgetRoute
    ) {
        budgetGraph(
            navController = navController,
            onNavigateToDashboard = { navController.navigate(DashboardRoute) }
        )
        dashboardGraph(
            navController = navController,
            onNavigateToBudget = { navController.navigate(BudgetRoute) }
        )
        expensesGraph(navController = navController)
        savingsGraph(navController = navController)
    }
}
```

---

## Package Structure

```
:presentation
└── budget/
    └── navigation/
        ├── BudgetRoutes.kt
        └── BudgetNavGraph.kt

:app
└── navigation/
    └── AppNavGraph.kt
```

---

## Naming Conventions

| Thing             | Convention                         | Example                            |
|-------------------|------------------------------------|------------------------------------|
| Route object      | `<Screen>Route`                    | `BudgetRoute`, `BudgetDetailRoute` |
| Feature nav graph | `NavGraphBuilder.<feature>Graph()` | `budgetGraph()`                    |

---

## Checklist: Adding Navigation to a New Feature

- [ ] Define `@Serializable` route objects in `feature/navigation/FeatureRoutes.kt`
- [ ] Create `NavGraphBuilder.<feature>Graph()` in `feature/navigation/FeatureNavGraph.kt`
- [ ] Wire `NavController` for intra-feature navigation
- [ ] Expose cross-feature destinations as lambda callbacks only
- [ ] Register the feature graph in `:app`'s `AppNavGraph.kt`