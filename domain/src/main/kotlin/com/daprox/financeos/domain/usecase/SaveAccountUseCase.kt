package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.repository.AccountRepository
import java.util.UUID

/** Creates a new account (empty id) or updates an existing one (non-empty id). */
class SaveAccountUseCase(private val repo: AccountRepository) {
    suspend operator fun invoke(account: Account): Result<Unit> =
        try {
            val toSave = if (account.id.isEmpty()) account.copy(id = UUID.randomUUID().toString()) else account
            if (account.id.isEmpty()) repo.insert(toSave) else repo.update(toSave)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
