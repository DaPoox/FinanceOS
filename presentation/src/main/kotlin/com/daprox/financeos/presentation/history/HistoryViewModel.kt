package com.daprox.financeos.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.domain.usecase.ObserveMonthsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

class HistoryViewModel(
    observeMonths: ObserveMonthsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state = _state.asStateFlow()

    init {
        observeMonths()
            .onEach { months ->
                val rows = months.map { month ->
                    MonthRowUiState(
                        monthLabel = month.label,
                        income = month.income,
                        spent = month.spent,
                        contrib = month.contrib,
                        status = MonthStatusEnum.valueOf(month.status.name),
                    )
                }
                val totalIncome = rows.sumOf { it.income }
                val totalContrib = rows.sumOf { it.contrib }
                val avgSavingRate = if (totalIncome > 0) ((totalContrib / totalIncome) * 100).roundToInt() else 0
                _state.value = HistoryUiState(
                    totalIncome = totalIncome,
                    totalContrib = totalContrib,
                    avgSavingRate = avgSavingRate,
                    barData = rows.reversed().map { it.contrib },
                    months = rows,
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: HistoryUiAction) = Unit
}
