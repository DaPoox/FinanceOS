package com.daprox.financeos.presentation

import com.daprox.financeos.presentation.accountform.AccountFormViewModel
import com.daprox.financeos.presentation.allocation.AllocationViewModel
import com.daprox.financeos.presentation.budget.BudgetViewModel
import com.daprox.financeos.presentation.dashboard.DashboardViewModel
import com.daprox.financeos.presentation.envelopedetail.EnvelopeDetailViewModel
import com.daprox.financeos.presentation.envelopeform.EnvelopeFormViewModel
import com.daprox.financeos.presentation.fixes.FixesViewModel
import com.daprox.financeos.presentation.history.HistoryViewModel
import com.daprox.financeos.presentation.patrimoine.PatrimoineViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { DashboardViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { BudgetViewModel(get(), get(), get(), get(), get()) }
    // AllocationViewModel now also receives AddEnvelopeToMonthUseCase
    viewModel { AllocationViewModel(get(), get(), get(), get(), get(), get()) }
    // EnvelopeDetailViewModel now also receives ArchiveEnvelopeUseCase
    viewModel { (id: String) -> EnvelopeDetailViewModel(id, get(), get(), get(), get(), get()) }
    viewModel { PatrimoineViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    // Account form — null accountId = create, non-null = edit
    viewModel { (accountId: String?) -> AccountFormViewModel(accountId, get(), get(), get()) }
    // Envelope form — null envelopeId = create; presetTypeKey pre-selects the type
    viewModel { (envelopeId: String?, presetTypeKey: String?) ->
        EnvelopeFormViewModel(envelopeId, presetTypeKey, get(), get(), get())
    }
    viewModel { FixesViewModel(get(), get(), get(), get()) }
}
