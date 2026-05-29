package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.AccountDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(private val dao: AccountDao) : AccountRepository {
    override fun getAllAccounts(): Flow<List<Account>> =
        dao.getAllAccounts().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Account? = dao.getById(id)?.toDomain()

    override suspend fun insert(account: Account) { dao.insert(account.toEntity()) }

    override suspend fun update(account: Account) { dao.update(account.toEntity()) }

    override suspend fun delete(id: String) { dao.deleteById(id) }
}
