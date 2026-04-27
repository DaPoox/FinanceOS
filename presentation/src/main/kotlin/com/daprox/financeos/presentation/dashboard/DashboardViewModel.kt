package com.daprox.financeos.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.daprox.financeos.presentation.dashboard.model.CategoryUi
import com.daprox.financeos.presentation.dashboard.model.LiquidityItemUi
import com.daprox.financeos.presentation.dashboard.model.NetWorthUi
import com.daprox.financeos.presentation.dashboard.model.ProgressBarUi
import com.daprox.financeos.presentation.dashboard.model.TransactionUi
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
                CategoryUi("Logement", "1 200 €", "40 %", 0.40f, androidx.compose.ui.graphics.Color(0xFF6EE591)),
                CategoryUi("Alimentation", "750 €", "25 %", 0.25f, androidx.compose.ui.graphics.Color(0xFF93C5FD)),
                CategoryUi("Transport", "600 €", "20 %", 0.20f, androidx.compose.ui.graphics.Color(0xFFC4B5FD)),
                CategoryUi("Loisirs", "450 €", "15 %", 0.15f, androidx.compose.ui.graphics.Color(0xFFF87171)),
            ),
            totalCategoryAmount = "3 000 €",
            liquidityItems = listOf(
                LiquidityItemUi(
                    label = "DISPONIBLE",
                    formattedAmount = "3 240 €",
                    subtitle = "Compte courant",
                ),
                LiquidityItemUi(
                    label = "ÉPARGNE",
                    formattedAmount = "8 500 €",
                    subtitle = "Livret A",
                ),
            ),
            recentTransactions = listOf(
                TransactionUi("1", "Carrefour", "Alimentation", "Aujourd'hui", "- 87,50 €", true, androidx.compose.ui.graphics.Color(0xFF93C5FD)),
                TransactionUi("2", "Salaire", "Revenus", "15 avr.", "+ 2 800 €", false, androidx.compose.ui.graphics.Color(0xFF6EE591)),
                TransactionUi("3", "Netflix", "Loisirs", "14 avr.", "- 17,99 €", true, androidx.compose.ui.graphics.Color(0xFFC4B5FD)),
                TransactionUi("4", "RATP", "Transport", "13 avr.", "- 1,90 €", true, androidx.compose.ui.graphics.Color(0xFFF87171)),
                TransactionUi("5", "Freelance", "Revenus", "12 avr.", "+ 650 €", false, androidx.compose.ui.graphics.Color(0xFF6EE591)),
            ),
        )
    )
    val state = _state.asStateFlow()

    fun onAction(action: DashboardUiAction) {
        when (action) {
            is DashboardUiAction.OnCategorySelected -> _state.update { current ->
                // Tap again on the same segment to deselect.
                val newIndex = if (current.selectedCategoryIndex == action.index) null else action.index
                current.copy(selectedCategoryIndex = newIndex)
            }

            // Navigation to add-transaction screen — wired when that screen is built.
            is DashboardUiAction.OnAddTransactionClick -> Unit
        }
    }
}
