package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daprox.financeos.data.db.entity.EnvelopeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvelopeDao {
    @Query("SELECT * FROM envelopes WHERE isActive = 1")
    fun getActiveEnvelopes(): Flow<List<EnvelopeEntity>>

    @Query("SELECT * FROM envelopes WHERE id = :id")
    suspend fun getById(id: String): EnvelopeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(envelope: EnvelopeEntity)

    @Update
    suspend fun update(envelope: EnvelopeEntity)

    @Query("UPDATE envelopes SET isActive = 0 WHERE id = :id")
    suspend fun softDelete(id: String)
}
