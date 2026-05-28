package com.daprox.financeos.presentation.history

sealed interface HistoryUiAction {
    data object OnRetry : HistoryUiAction
}
