package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daprox.financeos.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [TransactionEntity].
 *
 * Provides database operations for transactions including insert, delete, and querying
 * by month and by envelope-month combination.
 */
@Dao
interface TransactionDao {
  /**
   * Retrieves all transactions for a given month.
   *
   * @param monthId The month ID to filter by (format "YYYY-MM")
   * @return A [Flow] emitting the list of all transactions in this month
   *         whenever the database changes
   */
  @Query("SELECT * FROM transactions WHERE monthId = :monthId")
  fun getByMonth(monthId: String): Flow<List<TransactionEntity>>

  /**
   * Retrieves all transactions for a specific envelope within a specific month.
   *
   * @param envelopeId The envelope ID to filter by
   * @param monthId The month ID to filter by (format "YYYY-MM")
   * @return A [Flow] emitting the list of all transactions for this envelope in this month
   *         whenever the database changes
   */
  @Query("SELECT * FROM transactions WHERE envelopeId = :envelopeId AND monthId = :monthId")
  fun getByEnvelopeAndMonth(envelopeId: String, monthId: String): Flow<List<TransactionEntity>>

  /**
   * Inserts a new transaction or replaces if it already exists.
   *
   * @param tx The transaction to insert
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(tx: TransactionEntity)

  /**
   * Deletes a transaction.
   *
   * @param tx The transaction to delete (must have a valid ID)
   */
  @Delete
  suspend fun delete(tx: TransactionEntity)
}
