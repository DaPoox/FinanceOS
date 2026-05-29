package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.MonthAllocationDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MonthAllocationRepositoryImpl(private val dao: MonthAllocationDao) : MonthAllocationRepository {
    override fun getByMonth(monthId: String): Flow<List<MonthAllocation>> =
        dao.getByMonth(monthId).map { list -> list.map { it.toDomain() } }

    override suspend fun insert(allocation: MonthAllocation) {
        dao.insertAll(listOf(allocation.toEntity()))
    }

    override suspend fun insertAll(allocations: List<MonthAllocation>) {
        dao.insertAll(allocations.map { it.toEntity() })
    }

    override suspend fun deleteByMonth(monthId: String) { dao.deleteByMonth(monthId) }
}
