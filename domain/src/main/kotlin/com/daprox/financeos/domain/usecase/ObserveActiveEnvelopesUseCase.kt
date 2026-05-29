package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.repository.EnvelopeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes all active (non-archived) envelopes as a continuous reactive stream.
 *
 * Used by budget allocation, transaction, and envelope management screens
 * to display active envelopes and react to additions/archivings. Automatically
 * filters out archived envelopes.
 */
class ObserveActiveEnvelopesUseCase(private val repo: EnvelopeRepository) {
    /**
     * Invokes the use case to observe active envelopes.
     *
     * @return Flow that emits the active envelope list whenever changes occur
     */
    operator fun invoke(): Flow<List<Envelope>> = repo.getActiveEnvelopes()
}
