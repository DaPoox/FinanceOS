package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.MonthAllocationDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [MonthAllocationRepository] using Room database.
 *
 * Handles all database operations for month allocations, delegating to [MonthAllocationDao].
 * Transparently converts between [MonthAllocationEntity] (database model) and [MonthAllocation] (domain model).
 */
class MonthAllocationRepositoryImpl(private val dao: MonthAllocationDao) : MonthAllocationRepository {
  /**
   * Observes all allocations for a given month.
   *
   * @param monthId The month ID to filter by (format "YYYY-MM")
   * @return A [Flow] of domain model allocations for this month
   */
  override fun getByMonth(monthId: String): Flow<List<MonthAllocation>> =
    dao.getByMonth(monthId).map { list -> list.map { it.toDomain() } }

  /**
   * Inserts a single allocation into the database.
   *
   * @param allocation The domain model allocation to insert
   */
  override suspend fun insert(allocation: MonthAllocation) {
    dao.insertAll(listOf(allocation.toEntity()))
  }

  /**
   * Inserts multiple allocations into the database in a batch operation.
   *
   * Typically used when setting up the complete allocation for a month in a single operation.
   *
   * @param allocations The list of domain model allocations to insert
   */
  override suspend fun insertAll(allocations: List<MonthAllocation>) {
    dao.insertAll(allocations.map { it.toEntity() })
  }

  /**
   * Deletes all allocations for a given month.
   *
   * @param monthId The month ID to delete allocations for (format "YYYY-MM")
   */
  override suspend fun deleteByMonth(monthId: String) { dao.deleteByMonth(monthId) }
}
