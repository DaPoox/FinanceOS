package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.Month
import kotlinx.coroutines.flow.Flow

interface MonthRepository {
    fun getAllMonths(): Flow<List<Month>>
    suspend fun getById(monthId: String): Month?
    suspend fun insert(month: Month)
    suspend fun update(month: Month)
}
