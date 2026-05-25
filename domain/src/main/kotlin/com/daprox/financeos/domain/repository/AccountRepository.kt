package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun insert(account: Account)
    suspend fun update(account: Account)
}
