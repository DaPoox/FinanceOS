package com.daprox.financeos.presentation.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    fun onAction(action: DashboardUiAction) {
        when (action) {
            is DashboardUiAction.OnTabSelected -> _state.update { it.copy(selectedTab = action.tab) }
        }
    }
}
