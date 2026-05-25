package com.daprox.financeos.presentation.patrimoine

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val MOCK_ACCOUNTS = listOf(
    AccountUiState("Boursorama", AccountTypeEnum.COURANT, 4820.0, null, Color(0xFFE8EEF5)),
    AccountUiState("Livret A", AccountTypeEnum.EPARGNE, 18400.0, 22950.0, Color(0xFF7EB8F7)),
    AccountUiState("LDDS", AccountTypeEnum.EPARGNE, 8200.0, 12000.0, Color(0xFF7EB8F7)),
    AccountUiState("PEA", AccountTypeEnum.INVESTISSEMENT, 22680.0, null, Color(0xFFA78BFA)),
    AccountUiState("CTO Trade Rep.", AccountTypeEnum.INVESTISSEMENT, 6420.0, null, Color(0xFFA78BFA)),
)

private val SPARKLINE_6M = listOf(54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0)
private val SPARKLINE_6M_LABELS = listOf("déc", "jan", "fév", "mar", "avr", "mai")

private val SPARKLINE_12M = listOf(48200.0, 49100.0, 50320.0, 51100.0, 51980.0, 52900.0, 54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0)
private val SPARKLINE_12M_LABELS = listOf("jun", "jul", "aoû", "sep", "oct", "nov", "déc", "jan", "fév", "mar", "avr", "mai")

private val SPARKLINE_3Y = listOf(34000.0, 37500.0, 40200.0, 43800.0, 46100.0, 48200.0, 49100.0, 50320.0, 51100.0, 51980.0, 52900.0, 54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0)
private val SPARKLINE_3Y_LABELS = listOf("jun", "sep", "déc", "mar", "jun", "sep", "déc", "mar", "jun", "sep", "déc", "mar", "jun", "sep", "déc", "mar", "mai")

private fun sparklineFor(range: SparklineRangeEnum): Pair<List<Double>, List<String>> = when (range) {
    SparklineRangeEnum.M6 -> SPARKLINE_6M to SPARKLINE_6M_LABELS
    SparklineRangeEnum.M12 -> SPARKLINE_12M to SPARKLINE_12M_LABELS
    SparklineRangeEnum.Y3 -> SPARKLINE_3Y to SPARKLINE_3Y_LABELS
}

private fun buildInitialState(): PatrimoineUiState {
    val liquid = MOCK_ACCOUNTS.filter { it.type == AccountTypeEnum.COURANT }.sumOf { it.balance }
    val savings = MOCK_ACCOUNTS.filter { it.type == AccountTypeEnum.EPARGNE }.sumOf { it.balance }
    val investment = MOCK_ACCOUNTS.filter { it.type == AccountTypeEnum.INVESTISSEMENT }.sumOf { it.balance }
    val netWorth = liquid + savings + investment
    val (sparkData, sparkLabels) = sparklineFor(SparklineRangeEnum.M12)
    return PatrimoineUiState(
        netWorth = netWorth,
        deltaLabel = "+12 380 € · 6 mois",
        deltaPct = "+11.8%",
        savings = savings,
        investment = investment,
        liquid = liquid,
        sparklineData = sparkData,
        sparklineMonths = sparkLabels,
        selectedRange = SparklineRangeEnum.M12,
        accounts = MOCK_ACCOUNTS,
    )
}

class PatrimoineViewModel : ViewModel() {

    private val _state = MutableStateFlow(buildInitialState())
    val state = _state.asStateFlow()

    private val _events = Channel<PatrimoineUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: PatrimoineUiAction) {
        viewModelScope.launch {
            when (action) {
                is PatrimoineUiAction.OnRangeSelected -> {
                    val (sparkData, sparkLabels) = sparklineFor(action.range)
                    _state.update { it.copy(selectedRange = action.range, sparklineData = sparkData, sparklineMonths = sparkLabels) }
                }
            }
        }
    }
}
