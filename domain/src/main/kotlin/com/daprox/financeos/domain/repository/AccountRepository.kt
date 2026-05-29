package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.Account
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for account persistence and retrieval.
 *
 * Handles all account-related database operations. Implementations must provide
 * the data layer (Room) integration. Methods follow suspend/Flow patterns for
 * async operations and reactive updates.
 */
interface AccountRepository {
    /**
     * Observes all accounts as a continuous stream.
     *
     * @return Flow emitting the latest list of accounts whenever data changes
     */
    fun getAllAccounts(): Flow<List<Account>>

    /**
     * Retrieves a single account by ID.
     *
     * @param id Account identifier
     * @return Account if found, null otherwise
     */
    suspend fun getById(id: String): Account?

    /**
     * Inserts a new account into persistence.
     *
     * @param account Account to insert
     */
    suspend fun insert(account: Account)

    /**
     * Updates an existing account.
     *
     * @param account Account with updated values
     */
    suspend fun update(account: Account)

    /**
     * Deletes an account by ID.
     *
     * @param id Account identifier to delete
     */
    suspend fun delete(id: String)
}
