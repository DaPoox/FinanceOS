package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.Month
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for month period persistence and retrieval.
 *
 * Manages the monthly budget periods. Each month is a top-level budget container
 * that receives income and distributes funds to envelopes.
 */
interface MonthRepository {
    /**
     * Observes all months as a continuous stream.
     *
     * @return Flow emitting the latest list of all months
     */
    fun getAllMonths(): Flow<List<Month>>

    /**
     * Retrieves a single month by ID.
     *
     * @param monthId Month identifier in "YYYY-MM" format
     * @return Month if found, null otherwise
     */
    suspend fun getById(monthId: String): Month?

    /**
     * Inserts a new month period.
     *
     * @param month Month to insert
     */
    suspend fun insert(month: Month)

    /**
     * Updates an existing month (e.g., allocation status, spent amount).
     *
     * @param month Month with updated values
     */
    suspend fun update(month: Month)
}
