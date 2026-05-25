package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daprox.financeos.data.db.entity.MonthEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthDao {
    @Query("SELECT * FROM months ORDER BY id DESC")
    fun getAllMonths(): Flow<List<MonthEntity>>

    @Query("SELECT * FROM months WHERE id = :monthId")
    suspend fun getById(monthId: String): MonthEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(month: MonthEntity): Long

    @Update
    suspend fun update(month: MonthEntity): Int
}
