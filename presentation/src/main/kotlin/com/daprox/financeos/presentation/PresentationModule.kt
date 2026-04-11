package com.daprox.financeos.presentation

import org.koin.dsl.module

// Koin module for :presentation — registers all ViewModels.
// Assembled in :app via startKoin { modules(..., presentationModule) }.
val presentationModule = module {
    // ViewModels are registered here as features are added.
    // Example:
    // viewModelOf(::BudgetViewModel)
}
