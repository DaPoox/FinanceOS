package com.daprox.financeos.presentation.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

private val MOCK_MONTHS = listOf(
    MonthRowUiState("mai 26", income = 4200.0, spent = 2960.0, contrib = 1840.0, status = MonthStatusEnum.BEST),
    MonthRowUiState("avr 26", income = 4200.0, spent = 3210.0, contrib = 1410.0, status = MonthStatusEnum.GOOD),
    MonthRowUiState("mar 26", income = 3950.0, spent = 3380.0, contrib = 980.0, status = MonthStatusEnum.MID),
    MonthRowUiState("fév 26", income = 4400.0, spent = 3210.0, contrib = 1620.0, status = MonthStatusEnum.GOOD),
    MonthRowUiState("jan 26", income = 4200.0, spent = 3340.0, contrib = 1280.0, status = MonthStatusEnum.GOOD),
    MonthRowUiState("déc 25", income = 4800.0, spent = 4520.0, contrib = 520.0, status = MonthStatusEnum.HARD),
    MonthRowUiState("nov 25", income = 4200.0, spent = 3120.0, contrib = 1390.0, status = MonthStatusEnum.GOOD),
    MonthRowUiState("oct 25", income = 4200.0, spent = 3290.0, contrib = 1180.0, status = MonthStatusEnum.GOOD),
)

private fun buildState(): HistoryUiState {
    val totalIncome = MOCK_MONTHS.sumOf { it.income }
    val totalContrib = MOCK_MONTHS.sumOf { it.contrib }
    val avgSavingRate = ((totalContrib / totalIncome) * 100).roundToInt()
    val barData = MOCK_MONTHS.reversed().map { it.contrib }
    return HistoryUiState(
        totalIncome = totalIncome,
        totalContrib = totalContrib,
        avgSavingRate = avgSavingRate,
        barData = barData,
        months = MOCK_MONTHS,
    )
}

class HistoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(buildState())
    val state = _state.asStateFlow()

    fun onAction(action: HistoryUiAction) = Unit
}
