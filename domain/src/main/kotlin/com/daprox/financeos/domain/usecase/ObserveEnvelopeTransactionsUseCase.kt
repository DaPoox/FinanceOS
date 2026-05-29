package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Transaction
import com.daprox.financeos.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes all transactions for a specific envelope in a specific month.
 *
 * Used by envelope detail screens to display the transaction history
 * for a single envelope's allocation in a budget period.
 */
class ObserveEnvelopeTransactionsUseCase(private val repo: TransactionRepository) {
    /**
     * Invokes the use case to observe envelope transactions.
     *
     * @param envelopeId Envelope identifier
     * @param monthId Month in "YYYY-MM" format
     * @return Flow emitting the transaction list whenever changes occur
     */
    operator fun invoke(envelopeId: String, monthId: String): Flow<List<Transaction>> =
        repo.getByEnvelopeAndMonth(envelopeId, monthId)
}
