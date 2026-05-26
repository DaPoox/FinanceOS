package com.daprox.financeos.presentation

import com.daprox.financeos.presentation.allocation.AllocationViewModel
import com.daprox.financeos.presentation.budget.BudgetViewModel
import com.daprox.financeos.presentation.dashboard.DashboardViewModel
import com.daprox.financeos.presentation.envelopedetail.EnvelopeDetailViewModel
import com.daprox.financeos.presentation.history.HistoryViewModel
import com.daprox.financeos.presentation.patrimoine.PatrimoineViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { DashboardViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { BudgetViewModel(get(), get(), get(), get(), get()) }
    viewModel { AllocationViewModel(get(), get(), get(), get(), get()) }
    viewModel { (id: String) -> EnvelopeDetailViewModel(id, get(), get(), get(), get()) }
    viewModel { PatrimoineViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
}
