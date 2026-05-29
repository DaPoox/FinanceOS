package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daprox.financeos.data.db.entity.MonthAllocationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [MonthAllocationEntity].
 *
 * Provides database operations for month allocations including insert, delete,
 * and querying by month. Allocations define how much budget is assigned to each
 * envelope for a given month.
 */
@Dao
interface MonthAllocationDao {
  /**
   * Retrieves all allocations for a given month.
   *
   * @param monthId The month ID to filter by (format "YYYY-MM")
   * @return A [Flow] emitting the list of all allocations for this month
   *         whenever the database changes
   */
  @Query("SELECT * FROM month_allocations WHERE monthId = :monthId")
  fun getByMonth(monthId: String): Flow<List<MonthAllocationEntity>>

  /**
   * Inserts multiple allocations or replaces if they already exist.
   *
   * Typically used when setting up the complete allocation for a month in a single batch.
   *
   * @param allocations The list of allocations to insert
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(allocations: List<MonthAllocationEntity>)

  /**
   * Deletes all allocations for a given month.
   *
   * @param monthId The month ID to delete allocations for (format "YYYY-MM")
   */
  @Query("DELETE FROM month_allocations WHERE monthId = :monthId")
  suspend fun deleteByMonth(monthId: String)
}
