package com.daprox.financeos.presentation.patrimoine

sealed interface PatrimoineUiAction {
    data class OnRangeSelected(val range: SparklineRangeEnum) : PatrimoineUiAction
}
