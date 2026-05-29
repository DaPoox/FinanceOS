package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.repository.EnvelopeRepository
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import java.util.UUID

/**
 * Atomically inserts a new envelope and immediately allocates it for the given month.
 * Used from the NewEnvelopeSheet so the DB Flow picks up both changes at once.
 */
class AddEnvelopeToMonthUseCase(
    private val envelopeRepo: EnvelopeRepository,
    private val allocationRepo: MonthAllocationRepository,
) {
    suspend operator fun invoke(
        envelope: Envelope,
        monthId: String,
        amount: Double,
    ): Result<Unit> =
        try {
            val saved = if (envelope.id.isEmpty()) envelope.copy(id = UUID.randomUUID().toString()) else envelope
            envelopeRepo.insert(saved)
            allocationRepo.insert(
                MonthAllocation(
                    monthId = monthId,
                    envelopeId = saved.id,
                    allocated = amount,
                    accumulated = 0.0,
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
