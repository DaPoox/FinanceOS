package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.repository.AccountRepository

/**
 * Permanently deletes an account by ID.
 *
 * This is a destructive operation that fully removes the account from the database.
 * Unlike envelopes, accounts are hard-deleted (not soft-deleted).
 */
class DeleteAccountUseCase(private val repo: AccountRepository) {
    /**
     * Invokes the use case to delete an account.
     *
     * @param id Account ID to delete
     * @return [Result.Success] if account is deleted; [Result.Error] on failure
     */
    suspend operator fun invoke(id: String): Result<Unit> =
        try {
            repo.delete(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
