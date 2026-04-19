package com.daprox.financeos.presentation.dashboard.model

import androidx.compose.ui.graphics.Color

// UI model for a single transaction row in the recent transactions list.
data class TransactionUi(
    val id: String,
    val merchant: String,
    val category: String,
    val formattedDate: String,
    val formattedAmount: String, // includes sign: "- 45,00 €" / "+ 120,00 €"
    val isExpense: Boolean,
    val avatarColor: Color,      // color of the circular avatar — resolved from category at call site
)
