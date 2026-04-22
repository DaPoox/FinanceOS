package com.daprox.financeos.presentation.envelopes

import androidx.lifecycle.ViewModel
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory
import com.daprox.financeos.presentation.envelopes.model.EnvelopeUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EnvelopesViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        EnvelopesUiState(
            // Mock data — will be replaced with domain use case calls later.
            totalFormattedAmount = "8 400,00 €",
            envelopes = listOf(
                EnvelopeUi(
                    id                  = "fixed",
                    name                = "Fixed Expenses",
                    subtitle            = "Rent, utilities, insurance",
                    formattedAmount     = "2 940,00 €",
                    formattedPercentage = "35 %",
                    fraction            = 0.35f,
                    category            = EnvelopeCategory.FIXED_EXPENSES,
                    children            = listOf(
                        EnvelopeUi("fixed_rent",      "Rent",        "Monthly lease",       "1 200,00 €", "14 %", 0.14f, EnvelopeCategory.FIXED_EXPENSES),
                        EnvelopeUi("fixed_utilities", "Utilities",   "Electricity, water",  "  180,00 €",  "2 %", 0.02f, EnvelopeCategory.FIXED_EXPENSES),
                        EnvelopeUi("fixed_insurance", "Insurance",   "Home + health",       "  160,00 €",  "2 %", 0.02f, EnvelopeCategory.FIXED_EXPENSES),
                    ),
                ),
                EnvelopeUi(
                    id                  = "invest",
                    name                = "Investment",
                    subtitle            = "ETF, stocks, crypto",
                    formattedAmount     = "1 680,00 €",
                    formattedPercentage = "20 %",
                    fraction            = 0.20f,
                    category            = EnvelopeCategory.INVESTMENT,
                    children            = listOf(
                        EnvelopeUi("invest_etf",    "ETF",    "World index funds",  "1 000,00 €", "12 %", 0.12f, EnvelopeCategory.INVESTMENT),
                        EnvelopeUi("invest_crypto", "Crypto", "BTC, ETH",           "  680,00 €",  "8 %", 0.08f, EnvelopeCategory.INVESTMENT),
                    ),
                ),
                EnvelopeUi(
                    id                  = "savings",
                    name                = "Savings",
                    subtitle            = "Emergency fund, projects",
                    formattedAmount     = "1 260,00 €",
                    formattedPercentage = "15 %",
                    fraction            = 0.15f,
                    category            = EnvelopeCategory.SAVINGS,
                    children            = listOf(
                        EnvelopeUi("savings_emergency", "Emergency Fund", "3-month buffer",  "  840,00 €", "10 %", 0.10f, EnvelopeCategory.SAVINGS),
                        EnvelopeUi("savings_projects",  "Projects",       "Travel, gear",    "  420,00 €",  "5 %", 0.05f, EnvelopeCategory.SAVINGS),
                    ),
                ),
                EnvelopeUi(
                    id                  = "other",
                    name                = "Other",
                    subtitle            = "Food, leisure, misc",
                    formattedAmount     = "2 520,00 €",
                    formattedPercentage = "30 %",
                    fraction            = 0.30f,
                    category            = EnvelopeCategory.OTHER,
                    children            = listOf(
                        EnvelopeUi("other_food",    "Food",    "Groceries, restaurants", "  900,00 €", "11 %", 0.11f, EnvelopeCategory.OTHER),
                        EnvelopeUi("other_leisure", "Leisure", "Subscriptions, outings", "  720,00 €",  "9 %", 0.09f, EnvelopeCategory.OTHER),
                        EnvelopeUi("other_misc",    "Misc",    "Uncategorised",          "  900,00 €", "11 %", 0.10f, EnvelopeCategory.OTHER),
                    ),
                ),
            ),
        )
    )
    val state = _state.asStateFlow()

    fun onAction(action: EnvelopesUiAction) {
        when (action) {
            is EnvelopesUiAction.OnEnvelopeClick -> _state.update { current ->
                // Toggle: tapping the open row collapses it; tapping a different one opens it.
                val newExpanded = if (current.expandedId == action.id) null else action.id
                current.copy(expandedId = newExpanded)
            }

            // Navigation to add-allocation screen — wired when that screen is built.
            is EnvelopesUiAction.OnAddAllocationClick -> Unit
        }
    }
}
