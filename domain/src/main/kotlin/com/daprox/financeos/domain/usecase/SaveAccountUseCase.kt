package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.repository.AccountRepository
import java.util.UUID

/**
 * Creates or updates an account (insert for new, update for existing).
 *
 * Determines action based on whether the account has an empty or non-empty ID.
 * If ID is empty, a UUID is generated before persisting. This use case is the
 * primary entry point for account form submissions (create and edit flows).
 */
class SaveAccountUseCase(private val repo: AccountRepository) {
    /**
     * Invokes the use case to create or update an account.
     *
     * Logic:
     * - If account.id is empty: generate a UUID and insert as new
     * - If account.id is non-empty: update the existing account
     *
     * @param account Account to create or update
     * @return [Result.Success] if account is persisted; [Result.Error] on failure
     */
    suspend operator fun invoke(account: Account): Result<Unit> =
        try {
            val toSave = if (account.id.isEmpty()) account.copy(id = UUID.randomUUID().toString()) else account
            if (account.id.isEmpty()) repo.insert(toSave) else repo.update(toSave)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
