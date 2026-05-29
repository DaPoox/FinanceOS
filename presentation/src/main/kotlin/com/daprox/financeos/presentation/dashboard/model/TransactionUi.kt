package com.daprox.financeos.presentation.dashboard.model

import androidx.compose.ui.graphics.Color

/**
 * UI model for a single transaction row in the recent transactions list.
 *
 * @property id transaction identifier
 * @property merchant merchant/payee name
 * @property category transaction category
 * @property formattedDate formatted transaction date
 * @property formattedAmount formatted amount with sign (e.g., "- 45,00 €" or "+ 120,00 €")
 * @property isExpense true if expense, false if income
 * @property avatarColor category color for the circular avatar (resolved from theme at call site)
 */
data class TransactionUi(
    val id: String,
    val merchant: String,
    val category: String,
    val formattedDate: String,
    val formattedAmount: String,
    val isExpense: Boolean,
    val avatarColor: Color,
)
