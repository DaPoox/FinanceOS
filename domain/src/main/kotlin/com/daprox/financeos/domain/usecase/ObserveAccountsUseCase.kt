package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes all accounts as a continuous reactive stream.
 *
 * Used by screens and ViewModels to automatically display account lists
 * and react to account changes (creation, deletion, balance updates).
 */
class ObserveAccountsUseCase(private val repo: AccountRepository) {
    /**
     * Invokes the use case to observe all accounts.
     *
     * @return Flow that emits the account list whenever changes occur
     */
    operator fun invoke(): Flow<List<Account>> = repo.getAllAccounts()
}
