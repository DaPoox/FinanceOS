package com.daprox.financeos.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.daprox.financeos.presentation.dashboard.model.CategoryUi
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
            categories = listOf(
                CategoryUi("Logement",      "1 200 €", "40 %", 0.40f, androidx.compose.ui.graphics.Color(0xFF6EE591)),
                CategoryUi("Alimentation",  "750 €",   "25 %", 0.25f, androidx.compose.ui.graphics.Color(0xFF93C5FD)),
                CategoryUi("Transport",     "600 €",   "20 %", 0.20f, androidx.compose.ui.graphics.Color(0xFFC4B5FD)),
                CategoryUi("Loisirs",       "450 €",   "15 %", 0.15f, androidx.compose.ui.graphics.Color(0xFFF87171)),
            ),
        )
    )
    val state = _state.asStateFlow()

    fun onAction(action: DashboardUiAction) {
        when (action) {
            is DashboardUiAction.OnTabSelected ->
                _state.update { it.copy(selectedTab = action.tab) }

            is DashboardUiAction.OnCategorySelected -> _state.update { current ->
                // Tap again on the same segment to deselect.
                val newIndex = if (current.selectedCategoryIndex == action.index) null else action.index
                current.copy(selectedCategoryIndex = newIndex)
            }
        }
    }
}
