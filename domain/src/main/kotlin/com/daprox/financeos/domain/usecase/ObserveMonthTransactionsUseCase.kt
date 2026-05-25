package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Transaction
import com.daprox.financeos.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class ObserveMonthTransactionsUseCase(private val repo: TransactionRepository) {
    operator fun invoke(monthId: String): Flow<List<Transaction>> = repo.getByMonth(monthId)
}
