package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.model.MonthStatusEnum
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import com.daprox.financeos.domain.repository.MonthRepository

class AllocateMonthUseCase(
    private val monthRepo: MonthRepository,
    private val allocationRepo: MonthAllocationRepository,
) {
    suspend operator fun invoke(
        monthId: String,
        income: Double,
        allocations: List<MonthAllocation>,
    ): Result<Unit> {
        if (income <= 0) return Result.Error(IllegalArgumentException("Income must be positive"))
        if (allocations.isEmpty()) return Result.Error(IllegalArgumentException("Allocations must not be empty"))
        val existing = monthRepo.getById(monthId)
        val month = existing?.copy(income = income, isAllocated = true)
            ?: Month(
                id = monthId,
                label = monthIdToLabel(monthId),
                income = income,
                status = MonthStatusEnum.GOOD,
                isAllocated = true,
            )
        monthRepo.insert(month)
        allocationRepo.deleteByMonth(monthId)
        allocationRepo.insertAll(allocations)
        return Result.Success(Unit)
    }

    private fun monthIdToLabel(id: String): String {
        val parts = id.split("-")
        if (parts.size != 2) return id
        val monthNames = listOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre",
        )
        val monthIndex = parts[1].toIntOrNull()?.minus(1) ?: return id
        return "${monthNames.getOrElse(monthIndex) { id }} ${parts[0]}"
    }
}
