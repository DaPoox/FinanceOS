package com.daprox.financeos.di

import com.daprox.financeos.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/** App-level Koin module. Provides AppViewModel and related dependencies. */
val appModule = module {
  // ─── ViewModels ────────────────────────────────────────────────────────────
  viewModel { AppViewModel(get()) }
}
