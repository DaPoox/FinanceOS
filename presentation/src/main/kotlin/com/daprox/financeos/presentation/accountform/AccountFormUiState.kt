package com.daprox.financeos.presentation.accountform

import androidx.compose.runtime.Stable
import com.daprox.financeos.domain.model.AccountTypeEnum

/** Color options for the account avatar — mirrors the design palette. */
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

@Stable
data class AccountFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isError: Boolean = false,
    // null = create mode, non-null = edit mode (pre-populated)
    val accountId: String? = null,
    val name: String = "",
    val type: AccountTypeEnum = AccountTypeEnum.COURANT,
    val balance: String = "",
    val cap: String = "",
    val colorHex: String = "#e8eef5",
)
