package com.daprox.financeos.presentation

import com.daprox.financeos.presentation.allocation.AllocationViewModel
import com.daprox.financeos.presentation.budget.BudgetViewModel
import com.daprox.financeos.presentation.dashboard.DashboardViewModel
import com.daprox.financeos.presentation.envelopedetail.EnvelopeDetailViewModel
import com.daprox.financeos.presentation.envelopes.EnvelopesViewModel
import com.daprox.financeos.presentation.envelopes.edit.EnvelopesEditViewModel
import com.daprox.financeos.presentation.history.HistoryViewModel
import com.daprox.financeos.presentation.patrimoine.PatrimoineViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Koin module for :presentation — registers all ViewModels.
// Assembled in :app via startKoin { modules(..., presentationModule) }.
val presentationModule = module {
    viewModel { DashboardViewModel() }
    viewModel { BudgetViewModel() }
    viewModel { AllocationViewModel() }
    viewModel { (id: String) -> EnvelopeDetailViewModel(id) }
    viewModel { EnvelopesViewModel() }
    viewModel { EnvelopesEditViewModel() }
    viewModel { PatrimoineViewModel() }
    viewModel { HistoryViewModel() }
}
