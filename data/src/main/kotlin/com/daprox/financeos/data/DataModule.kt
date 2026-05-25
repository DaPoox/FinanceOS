package com.daprox.financeos.data

import com.daprox.financeos.data.db.FinanceDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { FinanceDatabase.create(androidContext()) }
    single { get<FinanceDatabase>().envelopeDao() }
    single { get<FinanceDatabase>().transactionDao() }
    single { get<FinanceDatabase>().monthDao() }
    single { get<FinanceDatabase>().monthAllocationDao() }
    single { get<FinanceDatabase>().accountDao() }
}
