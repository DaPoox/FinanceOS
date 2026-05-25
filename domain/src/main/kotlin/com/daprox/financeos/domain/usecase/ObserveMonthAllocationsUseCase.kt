package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import kotlinx.coroutines.flow.Flow

class ObserveMonthAllocationsUseCase(private val repo: MonthAllocationRepository) {
    operator fun invoke(monthId: String): Flow<List<MonthAllocation>> = repo.getByMonth(monthId)
}
