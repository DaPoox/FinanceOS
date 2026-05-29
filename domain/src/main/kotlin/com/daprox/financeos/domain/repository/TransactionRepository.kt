package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for transaction persistence and retrieval.
 *
 * Manages transaction records that track spending/income against envelopes
 * within specific months. Supports observation and filtering by month and envelope.
 */
interface TransactionRepository {
    /**
     * Observes all transactions for a specific month as a continuous stream.
     *
     * @param monthId Month identifier in "YYYY-MM" format
     * @return Flow emitting the latest list of transactions for the month
     */
    fun getByMonth(monthId: String): Flow<List<Transaction>>

    /**
     * Observes all transactions for a specific envelope in a specific month.
     *
     * @param envelopeId Envelope identifier
     * @param monthId Month identifier in "YYYY-MM" format
     * @return Flow emitting the latest list of transactions matching both filters
     */
    fun getByEnvelopeAndMonth(envelopeId: String, monthId: String): Flow<List<Transaction>>

    /**
     * Inserts a new transaction.
     *
     * @param tx Transaction to insert
     */
    suspend fun insert(tx: Transaction)

    /**
     * Deletes an existing transaction.
     *
     * @param tx Transaction to delete (matched by id)
     */
    suspend fun delete(tx: Transaction)
}
