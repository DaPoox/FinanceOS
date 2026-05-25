package com.daprox.financeos.domain.usecase

import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.repository.MonthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveCurrentMonthUseCase(private val repo: MonthRepository) {
    operator fun invoke(): Flow<Month?> = repo.getAllMonths().map { it.firstOrNull() }
}
