package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class ObserveAccountsUseCase(private val repo: AccountRepository) {
    operator fun invoke(): Flow<List<Account>> = repo.getAllAccounts()
}
