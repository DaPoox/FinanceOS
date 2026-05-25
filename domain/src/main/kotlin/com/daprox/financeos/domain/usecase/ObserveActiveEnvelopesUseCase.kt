package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.repository.EnvelopeRepository
import kotlinx.coroutines.flow.Flow

class ObserveActiveEnvelopesUseCase(private val repo: EnvelopeRepository) {
    operator fun invoke(): Flow<List<Envelope>> = repo.getActiveEnvelopes()
}
