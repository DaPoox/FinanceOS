package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import kotlinx.coroutines.flow.first

class CopyAllocationFromMonthUseCase(private val allocationRepo: MonthAllocationRepository) {
    suspend operator fun invoke(fromMonthId: String): List<MonthAllocation> =
        allocationRepo.getByMonth(fromMonthId).first()
}
