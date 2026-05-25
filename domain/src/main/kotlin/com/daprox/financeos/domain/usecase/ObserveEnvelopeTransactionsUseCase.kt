package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Transaction
import com.daprox.financeos.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class ObserveEnvelopeTransactionsUseCase(private val repo: TransactionRepository) {
    operator fun invoke(envelopeId: String, monthId: String): Flow<List<Transaction>> =
        repo.getByEnvelopeAndMonth(envelopeId, monthId)
}
