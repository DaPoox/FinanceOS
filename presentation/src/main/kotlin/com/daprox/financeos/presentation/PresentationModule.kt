package com.daprox.financeos.presentation

import com.daprox.financeos.presentation.dashboard.DashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Koin module for :presentation — registers all ViewModels.
// Assembled in :app via startKoin { modules(..., presentationModule) }.
val presentationModule = module {
    viewModel { DashboardViewModel() }
}
