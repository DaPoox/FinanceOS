package com.daprox.financeos.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.daprox.financeos.presentation.dashboard.model.NetWorthUi
import com.daprox.financeos.presentation.dashboard.model.ProgressBarUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        DashboardUiState(
            // Mock data — will be replaced with domain use case calls later.
            netWorth = NetWorthUi(
                label = "NET WORTH",
                formattedAmount = "12 450 €",
            ),
            progressBars = listOf(
                ProgressBarUi(
                    label = "MOIS",
                    formattedAmount = "2 400 €",
                    progress = 0.62f,
                    isGradient = false,
                ),
                ProgressBarUi(
                    label = "SEMAINE",
                    formattedAmount = "480 €",
                    progress = 0.45f,
                    isGradient = true,
                ),
            ),
        )
    )
    val state = _state.asStateFlow()

    fun onAction(action: DashboardUiAction) {
        when (action) {
            is DashboardUiAction.OnTabSelected -> _state.update { it.copy(selectedTab = action.tab) }
        }
    }
}
