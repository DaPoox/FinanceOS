package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daprox.financeos.data.db.entity.EnvelopeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [EnvelopeEntity].
 *
 * Provides database operations for envelopes including CRUD operations and soft deletion.
 * Envelopes are never hard-deleted; instead they are marked inactive via [softDelete].
 */
@Dao
interface EnvelopeDao {
  /**
   * Retrieves all active envelopes.
   *
   * @return A [Flow] emitting the list of all active envelopes (isActive = true)
   *         whenever the database changes
   */
  @Query("SELECT * FROM envelopes WHERE isActive = 1")
  fun getActiveEnvelopes(): Flow<List<EnvelopeEntity>>

  /**
   * Retrieves a single envelope by ID.
   *
   * @param id The envelope ID to look up
   * @return The [EnvelopeEntity] if found, null otherwise (suspending)
   */
  @Query("SELECT * FROM envelopes WHERE id = :id")
  suspend fun getById(id: String): EnvelopeEntity?

  /**
   * Inserts a new envelope or replaces if it already exists.
   *
   * @param envelope The envelope to insert
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(envelope: EnvelopeEntity)

  /**
   * Updates an existing envelope.
   *
   * @param envelope The envelope with updated values
   */
  @Update
  suspend fun update(envelope: EnvelopeEntity)

  /**
   * Soft-deletes an envelope by marking it inactive.
   *
   * The envelope record remains in the database but is hidden from queries
   * that filter by isActive = true.
   *
   * @param id The envelope ID to soft-delete
   */
  @Query("UPDATE envelopes SET isActive = 0 WHERE id = :id")
  suspend fun softDelete(id: String)
}
