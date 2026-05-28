package com.daprox.financeos.presentation.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.domain.usecase.ObserveMonthsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class HistoryViewModel(
    private val observeMonths: ObserveMonthsUseCase,
) : ViewModel() {

    private val _retryTrigger = MutableStateFlow(0)

    private val _state = MutableStateFlow(HistoryUiState())
    val state = _state.asStateFlow()

    init {
        _retryTrigger
            .flatMapLatest {
                observeMonths()
                    .map { months ->
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
                        HistoryUiState(
                            isLoading = false,
                            isEmpty = rows.isEmpty(),
                            totalIncome = totalIncome,
                            totalContrib = totalContrib,
                            avgSavingRate = avgSavingRate,
                            barData = rows.reversed().map { it.contrib },
                            months = rows,
                        )
                    }
                    .catch { e ->
                        Log.e("HistoryViewModel", "Flow error", e)
                        emit(HistoryUiState(isLoading = false, isError = true))
                    }
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun onAction(action: HistoryUiAction) {
        when (action) {
            is HistoryUiAction.OnRetry -> {
                _state.update { it.copy(isLoading = true, isError = false) }
                _retryTrigger.update { it + 1 }
            }
        }
    }
}
