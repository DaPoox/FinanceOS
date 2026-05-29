package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.repository.EnvelopeRepository

/** Soft-deletes an envelope so it no longer appears in active lists. */
class ArchiveEnvelopeUseCase(private val repo: EnvelopeRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        try {
            repo.softDelete(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
