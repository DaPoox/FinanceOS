package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.repository.EnvelopeRepository

/**
 * Archives an envelope by soft-deletion.
 *
 * The envelope is marked inactive and removed from active budget workflows,
 * but remains in the database for historical record-keeping. Archived envelopes
 * do not appear in allocation lists or new budget creation flows.
 */
class ArchiveEnvelopeUseCase(private val repo: EnvelopeRepository) {
    /**
     * Invokes the use case to archive an envelope.
     *
     * @param id Envelope ID to archive
     * @return [Result.Success] if envelope is archived; [Result.Error] on failure
     */
    suspend operator fun invoke(id: String): Result<Unit> =
        try {
            repo.softDelete(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
