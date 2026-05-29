package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Transaction
import com.daprox.financeos.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes all transactions for a specific month as a continuous reactive stream.
 *
 * Used by month detail and transaction list screens to display all spending
 * activity across all envelopes for a given budget period.
 */
class ObserveMonthTransactionsUseCase(private val repo: TransactionRepository) {
    /**
     * Invokes the use case to observe month transactions.
     *
     * @param monthId Month in "YYYY-MM" format
     * @return Flow emitting the transaction list whenever changes occur
     */
    operator fun invoke(monthId: String): Flow<List<Transaction>> = repo.getByMonth(monthId)
}
