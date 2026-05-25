package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daprox.financeos.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE monthId = :monthId")
    fun getByMonth(monthId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE envelopeId = :envelopeId AND monthId = :monthId")
    fun getByEnvelopeAndMonth(envelopeId: String, monthId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: TransactionEntity): Long

    @Delete
    suspend fun delete(tx: TransactionEntity): Int
}
