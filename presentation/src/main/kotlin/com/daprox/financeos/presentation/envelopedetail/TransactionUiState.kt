package com.daprox.financeos.presentation.envelopedetail

data class TransactionUiState(
    val id: String,
    val note: String,
    val dateLabel: String,
    val amount: Double,
)
