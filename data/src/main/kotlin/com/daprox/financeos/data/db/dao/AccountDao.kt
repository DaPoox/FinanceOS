package com.daprox.financeos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daprox.financeos.data.db.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [AccountEntity].
 *
 * Provides database operations for accounts including CRUD operations and querying.
 * All suspend functions execute on the database thread; Flow queries are observed reactively.
 */
@Dao
interface AccountDao {
  /**
   * Retrieves all accounts ordered by type and name.
   *
   * @return A [Flow] emitting the list of all accounts whenever the database changes
   */
  @Query("SELECT * FROM accounts ORDER BY type, name")
  fun getAllAccounts(): Flow<List<AccountEntity>>

  /**
   * Retrieves a single account by ID.
   *
   * @param id The account ID to look up
   * @return The [AccountEntity] if found, null otherwise (suspending)
   */
  @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
  suspend fun getById(id: String): AccountEntity?

  /**
   * Inserts a new account or replaces if it already exists.
   *
   * @param account The account to insert
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(account: AccountEntity)

  /**
   * Updates an existing account.
   *
   * @param account The account with updated values
   */
  @Update
  suspend fun update(account: AccountEntity)

  /**
   * Deletes an account by ID.
   *
   * @param id The account ID to delete
   */
  @Query("DELETE FROM accounts WHERE id = :id")
  suspend fun deleteById(id: String)
}
