package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.repository.EnvelopeRepository
import java.util.UUID

/**
 * Creates or updates an envelope (insert for new, update for existing).
 *
 * Determines action based on whether the envelope has an empty or non-empty ID.
 * If ID is empty, a UUID is generated before persisting. This use case is the
 * primary entry point for envelope form submissions (create and edit flows).
 */
class SaveEnvelopeUseCase(private val repo: EnvelopeRepository) {
    /**
     * Invokes the use case to create or update an envelope.
     *
     * Logic:
     * - If envelope.id is empty: generate a UUID and insert as new
     * - If envelope.id is non-empty: update the existing envelope
     *
     * @param envelope Envelope to create or update
     * @return [Result.Success] if envelope is persisted; [Result.Error] on failure
     */
    suspend operator fun invoke(envelope: Envelope): Result<Unit> =
        try {
            val toSave = if (envelope.id.isEmpty()) envelope.copy(id = UUID.randomUUID().toString()) else envelope
            if (envelope.id.isEmpty()) repo.insert(toSave) else repo.update(toSave)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
