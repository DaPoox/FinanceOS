package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daprox.financeos.data.db.entity.MonthEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [MonthEntity].
 *
 * Provides database operations for months including CRUD operations.
 * Months are identified by their ID in format "YYYY-MM".
 */
@Dao
interface MonthDao {
  /**
   * Retrieves all months ordered by ID in descending order (newest first).
   *
   * @return A [Flow] emitting the list of all months whenever the database changes
   */
  @Query("SELECT * FROM months ORDER BY id DESC")
  fun getAllMonths(): Flow<List<MonthEntity>>

  /**
   * Retrieves a single month by ID.
   *
   * @param monthId The month ID to look up (format "YYYY-MM", e.g., "2026-05")
   * @return The [MonthEntity] if found, null otherwise (suspending)
   */
  @Query("SELECT * FROM months WHERE id = :monthId")
  suspend fun getById(monthId: String): MonthEntity?

  /**
   * Inserts a new month or replaces if it already exists.
   *
   * @param month The month to insert
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(month: MonthEntity)

  /**
   * Updates an existing month.
   *
   * @param month The month with updated values
   */
  @Update
  suspend fun update(month: MonthEntity)
}
