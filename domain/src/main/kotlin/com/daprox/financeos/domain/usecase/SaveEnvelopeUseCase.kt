package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.repository.EnvelopeRepository
import java.util.UUID

/** Creates a new envelope (empty id) or updates an existing one (non-empty id). */
class SaveEnvelopeUseCase(private val repo: EnvelopeRepository) {
    suspend operator fun invoke(envelope: Envelope): Result<Unit> =
        try {
            val toSave = if (envelope.id.isEmpty()) envelope.copy(id = UUID.randomUUID().toString()) else envelope
            if (envelope.id.isEmpty()) repo.insert(toSave) else repo.update(toSave)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
