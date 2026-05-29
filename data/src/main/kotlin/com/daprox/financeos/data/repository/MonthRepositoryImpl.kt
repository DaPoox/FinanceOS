package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.MonthDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.repository.MonthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [MonthRepository] using Room database.
 *
 * Handles all database operations for months, delegating to [MonthDao].
 * Transparently converts between [MonthEntity] (database model) and [Month] (domain model).
 */
class MonthRepositoryImpl(private val dao: MonthDao) : MonthRepository {
  /**
   * Observes all months ordered by ID in descending order (newest first).
   *
   * @return A [Flow] of domain model months
   */
  override fun getAllMonths(): Flow<List<Month>> =
    dao.getAllMonths().map { list -> list.map { it.toDomain() } }

  /**
   * Retrieves a single month by ID.
   *
   * @param monthId The month ID to look up (format "YYYY-MM", e.g., "2026-05")
   * @return The domain model month or null if not found
   */
  override suspend fun getById(monthId: String): Month? = dao.getById(monthId)?.toDomain()

  /**
   * Inserts a new month into the database.
   *
   * @param month The domain model month to insert
   */
  override suspend fun insert(month: Month) { dao.insert(month.toEntity()) }

  /**
   * Updates an existing month in the database.
   *
   * @param month The domain model month with updated values
   */
  override suspend fun update(month: Month) { dao.update(month.toEntity()) }
}
