package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.model.MonthAllocation
import com.daprox.financeos.domain.model.MonthStatusEnum
import com.daprox.financeos.domain.repository.MonthAllocationRepository
import com.daprox.financeos.domain.repository.MonthRepository

/**
 * Allocates monthly income across envelopes.
 *
 * The core workflow: validate income and allocations, create or update the month record,
 * and persist all allocations. Replaces prior allocations for the month atomically.
 * If the month doesn't exist, it is created with a default status of [MonthStatusEnum.GOOD].
 *
 * The [monthIdToLabel] helper converts "YYYY-MM" IDs to French month labels (e.g., "Janvier 2025").
 */
class AllocateMonthUseCase(
    private val monthRepo: MonthRepository,
    private val allocationRepo: MonthAllocationRepository,
) {
    /**
     * Invokes the use case to allocate a month's income to envelopes.
     *
     * Validates:
     * - income > 0 (must have positive income)
     * - allocations.isNotEmpty (at least one envelope must be funded)
     *
     * Creates or updates the month record, then replaces all its allocations.
     *
     * @param monthId Month in "YYYY-MM" format
     * @param income Total income available for allocation
     * @param allocations List of envelope allocations to apply
     * @return [Result.Success] if month and allocations are saved; [Result.Error] on validation failure
     */
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

    /**
     * Converts a month ID in "YYYY-MM" format to a French month label.
     *
     * Example: "2025-01" -> "Janvier 2025"
     *
     * @param id Month ID string
     * @return Formatted label, or the ID itself if parsing fails
     */
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
