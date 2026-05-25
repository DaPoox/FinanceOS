package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.MonthDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.repository.MonthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MonthRepositoryImpl(private val dao: MonthDao) : MonthRepository {
    override fun getAllMonths(): Flow<List<Month>> =
        dao.getAllMonths().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(monthId: String): Month? = dao.getById(monthId)?.toDomain()

    override suspend fun insert(month: Month) { dao.insert(month.toEntity()) }

    override suspend fun update(month: Month) { dao.update(month.toEntity()) }
}
