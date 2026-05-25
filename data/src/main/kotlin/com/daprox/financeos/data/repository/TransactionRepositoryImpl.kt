package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.TransactionDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.Transaction
import com.daprox.financeos.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(private val dao: TransactionDao) : TransactionRepository {
    override fun getByMonth(monthId: String): Flow<List<Transaction>> =
        dao.getByMonth(monthId).map { list -> list.map { it.toDomain() } }

    override fun getByEnvelopeAndMonth(envelopeId: String, monthId: String): Flow<List<Transaction>> =
        dao.getByEnvelopeAndMonth(envelopeId, monthId).map { list -> list.map { it.toDomain() } }

    override suspend fun insert(tx: Transaction) { dao.insert(tx.toEntity()) }

    override suspend fun delete(tx: Transaction) { dao.delete(tx.toEntity()) }
}
