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

/**
 * Koin dependency injection module for the presentation layer.
 *
 * Registers all ViewModels with their use case dependencies. ViewModels that support multiple
 * modes (create/edit) use parameter-based factories. Chart and report screens are read-only and
 * use simple [viewModel] bindings.
 *
 * ## ViewModel Bindings
 *
 * - **DashboardViewModel**: Root dashboard screen, aggregates month status and envelope overview
 * - **BudgetViewModel**: Month budget tracking with allocations and transactions
 * - **AllocationViewModel**: Monthly allocation editor with envelope income distribution
 * - **EnvelopeDetailViewModel**: Single envelope detail page with transaction list (parametrized)
 * - **PatrimoineViewModel**: Net worth / wealth screen with account breakdown and sparkline
 * - **HistoryViewModel**: 12-month financial summary with savings rate analysis
 * - **AccountFormViewModel**: Create/edit bank account form (parametrized, accountId determines mode)
 * - **EnvelopeFormViewModel**: Create/edit budget envelope form (parametrized, envelopeId + presetTypeKey)
 * - **FixesViewModel**: Fixed charges tracking screen
 */
val presentationModule = module {
    viewModel { DashboardViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { BudgetViewModel(get(), get(), get(), get(), get()) }
    viewModel { AllocationViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { (id: String) -> EnvelopeDetailViewModel(id, get(), get(), get(), get(), get()) }
    viewModel { PatrimoineViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { (accountId: String?) -> AccountFormViewModel(accountId, get(), get(), get()) }
    viewModel { (envelopeId: String?, presetTypeKey: String?) ->
        EnvelopeFormViewModel(envelopeId, presetTypeKey, get(), get(), get())
    }
    viewModel { FixesViewModel(get(), get(), get(), get()) }
}
