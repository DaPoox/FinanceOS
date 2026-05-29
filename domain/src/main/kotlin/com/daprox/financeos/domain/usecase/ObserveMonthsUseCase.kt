package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.repository.MonthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes all months as a continuous reactive stream.
 *
 * Used by month history, date pickers, and budget planning screens to display
 * all budget periods and react to new month creation or status changes.
 */
class ObserveMonthsUseCase(private val repo: MonthRepository) {
    /**
     * Invokes the use case to observe all months.
     *
     * @return Flow emitting the month list whenever changes occur
     */
    operator fun invoke(): Flow<List<Month>> = repo.getAllMonths()
}
