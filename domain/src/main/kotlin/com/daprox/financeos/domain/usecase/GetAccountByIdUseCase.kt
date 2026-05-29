package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.repository.AccountRepository

/** Fetches a single account by its id, or null if not found. */
class GetAccountByIdUseCase(private val repo: AccountRepository) {
    suspend operator fun invoke(id: String): Account? = repo.getById(id)
}
