package com.daprox.financeos.presentation.patrimoine

import androidx.compose.runtime.Stable

/**
 * UI state for the patrimoine (net worth / wealth) screen.
 *
 * Displays net worth summary, account breakdown by type, sparkline chart with selectable ranges,
 * and detailed account list. Aggregate balances are computed from all accounts; chart data is
 * mock (hardcoded sparkline datasets per range).
 *
 * @property isLoading loading state while fetching accounts
 * @property isError error state if data fetch fails
 * @property isEmpty empty state when no accounts exist (CTA to add first account)
 * @property netWorth total wealth across all accounts (liquid + savings + investment)
 * @property deltaLabel text label for wealth change over period (e.g. "+12 380 € · 6 mois")
 * @property deltaPct percentage change text (e.g. "+11.8%")
 * @property savings sum of EPARGNE account balances
 * @property investment sum of INVESTISSEMENT account balances
 * @property liquid sum of COURANT account balances
 * @property sparklineData chart values for selected range (mock data per range)
 * @property sparklineMonths month labels for chart (e.g. ["jan", "fév", "mar", ...])
 * @property selectedRange currently selected chart range (M6, M12, Y3)
 * @property accounts list of account rows for display
 */
@Stable
data class PatrimoineUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val netWorth: Double = 0.0,
    val deltaLabel: String = "",
    val deltaPct: String = "",
    val savings: Double = 0.0,
    val investment: Double = 0.0,
    val liquid: Double = 0.0,
    val sparklineData: List<Double> = emptyList(),
    val sparklineMonths: List<String> = emptyList(),
    val selectedRange: SparklineRangeEnum = SparklineRangeEnum.M12,
    val accounts: List<AccountUiState> = emptyList(),
)
