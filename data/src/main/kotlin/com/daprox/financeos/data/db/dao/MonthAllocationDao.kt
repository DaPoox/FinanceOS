package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daprox.financeos.data.db.entity.MonthAllocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthAllocationDao {
    @Query("SELECT * FROM month_allocations WHERE monthId = :monthId")
    fun getByMonth(monthId: String): Flow<List<MonthAllocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(allocations: List<MonthAllocationEntity>): List<Long>

    @Query("DELETE FROM month_allocations WHERE monthId = :monthId")
    suspend fun deleteByMonth(monthId: String): Int
}
