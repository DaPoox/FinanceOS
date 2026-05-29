package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.TransactionDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.Transaction
import com.daprox.financeos.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [TransactionRepository] using Room database.
 *
 * Handles all database operations for transactions, delegating to [TransactionDao].
 * Transparently converts between [TransactionEntity] (database model) and [Transaction] (domain model).
 */
class TransactionRepositoryImpl(private val dao: TransactionDao) : TransactionRepository {
  /**
   * Observes all transactions for a given month.
   *
   * @param monthId The month ID to filter by (format "YYYY-MM")
   * @return A [Flow] of domain model transactions for this month
   */
  override fun getByMonth(monthId: String): Flow<List<Transaction>> =
    dao.getByMonth(monthId).map { list -> list.map { it.toDomain() } }

  /**
   * Observes all transactions for a specific envelope within a specific month.
   *
   * @param envelopeId The envelope ID to filter by
   * @param monthId The month ID to filter by (format "YYYY-MM")
   * @return A [Flow] of domain model transactions for this envelope in this month
   */
  override fun getByEnvelopeAndMonth(envelopeId: String, monthId: String): Flow<List<Transaction>> =
    dao.getByEnvelopeAndMonth(envelopeId, monthId).map { list -> list.map { it.toDomain() } }

  /**
   * Inserts a new transaction into the database.
   *
   * @param tx The domain model transaction to insert
   */
  override suspend fun insert(tx: Transaction) { dao.insert(tx.toEntity()) }

  /**
   * Deletes a transaction from the database.
   *
   * @param tx The domain model transaction to delete (must have a valid ID)
   */
  override suspend fun delete(tx: Transaction) { dao.delete(tx.toEntity()) }
}
