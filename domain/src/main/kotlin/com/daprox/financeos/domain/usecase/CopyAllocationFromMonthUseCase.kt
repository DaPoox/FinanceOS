package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import kotlinx.coroutines.flow.first

/**
 * Retrieves all allocations from a specific month for copying to a new month.
 *
 * Used to streamline budget setup by replicating envelope allocations from
 * a prior month. The caller then modifies amounts and applies the updated
 * allocations to the target month via [AllocateMonthUseCase].
 */
class CopyAllocationFromMonthUseCase(private val allocationRepo: MonthAllocationRepository) {
    /**
     * Invokes the use case to fetch allocations from a source month.
     *
     * @param fromMonthId Source month in "YYYY-MM" format
     * @return List of allocations for the month (empty if month not found)
     */
    suspend operator fun invoke(fromMonthId: String): List<MonthAllocation> =
        allocationRepo.getByMonth(fromMonthId).first()
}
