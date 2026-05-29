package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.repository.MonthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Observes the current/active month as a continuous reactive stream.
 *
 * Assumes the first month in the list is the "current" month. Used by the home
 * screen and budget allocation workflows to display and react to the active
 * budget period.
 */
class ObserveCurrentMonthUseCase(private val repo: MonthRepository) {
    /**
     * Invokes the use case to observe the current month.
     *
     * @return Flow that emits the current month (first in list) or null if no months exist
     */
    operator fun invoke(): Flow<Month?> = repo.getAllMonths().map { it.firstOrNull() }
}
