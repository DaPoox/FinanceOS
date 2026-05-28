package com.daprox.financeos.di

import com.daprox.financeos.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AppViewModel(get()) }
}
