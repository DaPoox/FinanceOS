package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.repository.EnvelopeRepository
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import java.util.UUID

/**
 * Creates a new envelope and immediately allocates funds to it for a specific month.
 *
 * This use case combines envelope creation and allocation in a single operation,
 * ensuring the UI observes both changes atomically. Commonly triggered by the
 * "New Envelope" sheet during budget allocation workflow.
 *
 * If the envelope has an empty ID, a UUID is generated. Side effect: persists
 * envelope to the database and creates an allocation record.
 */
class AddEnvelopeToMonthUseCase(
    private val envelopeRepo: EnvelopeRepository,
    private val allocationRepo: MonthAllocationRepository,
) {
    /**
     * Invokes the use case to create and allocate an envelope.
     *
     * @param envelope Envelope to create; if ID is empty, a UUID will be generated
     * @param monthId Target month in "YYYY-MM" format
     * @param amount Amount to allocate to the envelope for this month
     * @return [Result.Success] if envelope and allocation are created; [Result.Error] on failure
     */
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
