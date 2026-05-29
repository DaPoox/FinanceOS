package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.repository.AccountRepository

/** Permanently removes an account by id. */
class DeleteAccountUseCase(private val repo: AccountRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        try {
            repo.delete(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
