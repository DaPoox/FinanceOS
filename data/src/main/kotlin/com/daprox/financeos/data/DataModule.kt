package com.daprox.financeos.data

import android.content.Context
import com.daprox.financeos.data.db.FinanceDatabase
import com.daprox.financeos.data.manager.FirstLaunchManager
import com.daprox.financeos.data.repository.AccountRepositoryImpl
import com.daprox.financeos.data.repository.EnvelopeRepositoryImpl
import com.daprox.financeos.data.repository.MonthAllocationRepositoryImpl
import com.daprox.financeos.data.repository.MonthRepositoryImpl
import com.daprox.financeos.data.repository.TransactionRepositoryImpl
import com.daprox.financeos.domain.repository.AccountRepository
import com.daprox.financeos.domain.repository.EnvelopeRepository
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import com.daprox.financeos.domain.repository.MonthRepository
import com.daprox.financeos.domain.repository.TransactionRepository
import com.daprox.financeos.domain.usecase.AddTransactionUseCase
import com.daprox.financeos.domain.usecase.AllocateMonthUseCase
import com.daprox.financeos.domain.usecase.CopyAllocationFromMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveAccountsUseCase
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.ObserveCurrentMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveEnvelopeTransactionsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthAllocationsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthTransactionsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthsUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    // Database
    single { FinanceDatabase.create(androidContext()) }

    // Managers
    single { FirstLaunchManager(androidContext().getSharedPreferences("fos_prefs", Context.MODE_PRIVATE)) }

    // DAOs
    single { get<FinanceDatabase>().envelopeDao() }
    single { get<FinanceDatabase>().transactionDao() }
    single { get<FinanceDatabase>().monthDao() }
    single { get<FinanceDatabase>().monthAllocationDao() }
    single { get<FinanceDatabase>().accountDao() }

    // Repositories
    single<EnvelopeRepository> { EnvelopeRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<MonthRepository> { MonthRepositoryImpl(get()) }
    single<MonthAllocationRepository> { MonthAllocationRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }

    // Use Cases
    single { ObserveActiveEnvelopesUseCase(get()) }
    single { ObserveMonthsUseCase(get()) }
    single { ObserveCurrentMonthUseCase(get()) }
    single { ObserveMonthAllocationsUseCase(get()) }
    single { ObserveMonthTransactionsUseCase(get()) }
    single { ObserveEnvelopeTransactionsUseCase(get()) }
    single { ObserveAccountsUseCase(get()) }
    single { AddTransactionUseCase(get()) }
    single { AllocateMonthUseCase(get(), get()) }
    single { CopyAllocationFromMonthUseCase(get()) }
}
