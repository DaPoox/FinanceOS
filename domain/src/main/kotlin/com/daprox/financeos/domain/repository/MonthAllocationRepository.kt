package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.MonthAllocation
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for month allocation persistence and retrieval.
 *
 * Manages the allocation records that link envelopes to specific months.
 * Allocations define how much budget is assigned to each envelope for a given month.
 */
interface MonthAllocationRepository {
    /**
     * Observes all allocations for a specific month as a continuous stream.
     *
     * @param monthId Month identifier in "YYYY-MM" format
     * @return Flow emitting the latest list of allocations for the month
     */
    fun getByMonth(monthId: String): Flow<List<MonthAllocation>>

    /**
     * Inserts a single allocation record.
     *
     * @param allocation Allocation to insert
     */
    suspend fun insert(allocation: MonthAllocation)

    /**
     * Batch-inserts multiple allocation records (e.g., when allocating a month).
     *
     * @param allocations List of allocations to insert
     */
    suspend fun insertAll(allocations: List<MonthAllocation>)

    /**
     * Deletes all allocations for a specific month.
     *
     * @param monthId Month identifier to delete allocations for
     */
    suspend fun deleteByMonth(monthId: String)
}
