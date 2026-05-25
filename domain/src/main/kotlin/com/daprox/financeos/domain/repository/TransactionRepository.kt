package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getByMonth(monthId: String): Flow<List<Transaction>>
    fun getByEnvelopeAndMonth(envelopeId: String, monthId: String): Flow<List<Transaction>>
    suspend fun insert(tx: Transaction)
    suspend fun delete(tx: Transaction)
}
