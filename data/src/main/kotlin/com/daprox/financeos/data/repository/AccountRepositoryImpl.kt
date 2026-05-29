package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.AccountDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [AccountRepository] using Room database.
 *
 * Handles all database operations for accounts, delegating to [AccountDao].
 * Transparently converts between [AccountEntity] (database model) and [Account] (domain model).
 */
class AccountRepositoryImpl(private val dao: AccountDao) : AccountRepository {
  /**
   * Observes all accounts ordered by type and name.
   *
   * @return A [Flow] of domain model accounts
   */
  override fun getAllAccounts(): Flow<List<Account>> =
    dao.getAllAccounts().map { list -> list.map { it.toDomain() } }

  /**
   * Retrieves a single account by ID.
   *
   * @param id The account ID
   * @return The domain model account or null if not found
   */
  override suspend fun getById(id: String): Account? = dao.getById(id)?.toDomain()

  /**
   * Inserts a new account into the database.
   *
   * @param account The domain model account to insert
   */
  override suspend fun insert(account: Account) { dao.insert(account.toEntity()) }

  /**
   * Updates an existing account in the database.
   *
   * @param account The domain model account with updated values
   */
  override suspend fun update(account: Account) { dao.update(account.toEntity()) }

  /**
   * Deletes an account by ID.
   *
   * @param id The account ID to delete
   */
  override suspend fun delete(id: String) { dao.deleteById(id) }
}
