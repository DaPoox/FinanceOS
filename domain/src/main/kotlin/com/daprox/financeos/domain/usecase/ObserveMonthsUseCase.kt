package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.repository.MonthRepository
import kotlinx.coroutines.flow.Flow

class ObserveMonthsUseCase(private val repo: MonthRepository) {
    operator fun invoke(): Flow<List<Month>> = repo.getAllMonths()
}
