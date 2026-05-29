package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.repository.AccountRepository

/**
 * Fetches a single account by ID (one-shot query, not observable).
 *
 * Used for lookups during account editing or detail screens. Does not emit
 * updates if the account changes later; use [ObserveAccountsUseCase] for
 * reactive observation.
 */
class GetAccountByIdUseCase(private val repo: AccountRepository) {
    /**
     * Invokes the use case to fetch an account by ID.
     *
     * @param id Account identifier
     * @return Account if found, null otherwise
     */
    suspend operator fun invoke(id: String): Account? = repo.getById(id)
}
