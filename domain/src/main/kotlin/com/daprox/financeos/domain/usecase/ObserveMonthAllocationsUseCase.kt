package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes all allocations for a specific month as a continuous reactive stream.
 *
 * Used by budget allocation screens and expense tracking to display which
 * envelopes are funded and by how much for a given month period.
 */
class ObserveMonthAllocationsUseCase(private val repo: MonthAllocationRepository) {
    /**
     * Invokes the use case to observe month allocations.
     *
     * @param monthId Month in "YYYY-MM" format
     * @return Flow emitting the allocation list whenever changes occur
     */
    operator fun invoke(monthId: String): Flow<List<MonthAllocation>> = repo.getByMonth(monthId)
}
