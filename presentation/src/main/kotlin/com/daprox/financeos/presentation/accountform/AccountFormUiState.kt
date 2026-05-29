package com.daprox.financeos.presentation.accountform

import androidx.compose.runtime.Stable
import com.daprox.financeos.domain.model.AccountTypeEnum

/**
 * Hex color palette for account avatar backgrounds.
 *
 * Maps to the design system color tokens used across budgeting screens.
 */
val ACCOUNT_COLOR_OPTIONS = listOf(
    "#e8eef5",  // neutral (Boursorama-style)
    "#7eb8f7",  // savings blue
    "#a78bfa",  // investment purple
    "#22c55e",  // green
    "#f0b429",  // primary gold
    "#fb923c",  // orange
    "#f87171",  // red
    "#5eead4",  // teal
)

/**
 * UI state for the account form screen (create or edit mode).
 *
 * Tracks form input, loading/saving states, and validation errors. Supports three account types:
 * COURANT (checking), EPARGNE (savings), and INVESTISSEMENT (investment). The cap field is
 * only applicable to EPARGNE accounts (max balance like Livret A).
 *
 * @property isLoading loading state while fetching account data in edit mode
 * @property isSaving saving state during form submission
 * @property isError error state if data loading fails
 * @property accountId null = create mode; non-null = edit mode for that account
 * @property name account display name (max 50 chars typically)
 * @property type account classification (COURANT, EPARGNE, INVESTISSEMENT)
 * @property balance current balance as a string
 * @property cap maximum balance (EPARGNE accounts only, e.g. Livret A: 22950€)
 * @property colorHex hex color for account avatar background
 */
@Stable
data class AccountFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isError: Boolean = false,
    val accountId: String? = null,
    val name: String = "",
    val type: AccountTypeEnum = AccountTypeEnum.COURANT,
    val balance: String = "",
    val cap: String = "",
    val colorHex: String = "#e8eef5",
)
