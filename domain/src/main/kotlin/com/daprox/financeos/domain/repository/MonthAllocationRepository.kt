package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.MonthAllocation
import kotlinx.coroutines.flow.Flow

interface MonthAllocationRepository {
    fun getByMonth(monthId: String): Flow<List<MonthAllocation>>
    suspend fun insert(allocation: MonthAllocation)
    suspend fun insertAll(allocations: List<MonthAllocation>)
    suspend fun deleteByMonth(monthId: String)
}
