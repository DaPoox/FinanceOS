package com.daprox.financeos.data

import org.koin.dsl.module

// Koin module for :data — registers the Room database, DAOs, and repository implementations.
// Assembled in :app via startKoin { modules(dataModule, ...) }.
val dataModule = module {
    // Room database and DAOs are registered here as features are added.
    // Example:
    // single { FinanceDatabase.create(get()) }
    // single { get<FinanceDatabase>().budgetDao() }
    // singleOf(::BudgetRepositoryImpl) { bind<BudgetRepository>() }
}
