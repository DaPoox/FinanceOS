package com.daprox.financeos.presentation.envelopedetail

/**
 * UI model for a transaction in the Envelope Detail screen's transaction list.
 *
 * Represents a single transaction for the current envelope this month.
 *
 * @param id The transaction's unique identifier
 * @param note The transaction description/note (e.g., "Restaurants", "Courses")
 * @param dateLabel Localized date label (e.g., "15 mai")
 * @param amount The transaction amount
 */
data class TransactionUiState(
    val id: String,
    val note: String,
    val dateLabel: String,
    val amount: Double,
)
