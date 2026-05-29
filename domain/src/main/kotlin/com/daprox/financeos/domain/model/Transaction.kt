package com.daprox.financeos.domain.model

/**
 * Represents a single financial transaction.
 *
 * A transaction records spending or income against a specific envelope and month.
 * Each transaction is linked to an allocation and tracked for budget management and
 * historical record-keeping.
 *
 * @property id Unique identifier for the transaction
 * @property envelopeId ID of the envelope this transaction belongs to
 * @property monthId Month identifier ("YYYY-MM") that this transaction is attributed to
 * @property amount Transaction amount (positive for spending/income, negative for refunds)
 * @property note Optional user-provided description or memo
 * @property date Timestamp of the transaction (milliseconds since epoch)
 */
data class Transaction(
    val id: String,
    val envelopeId: String,
    val monthId: String,
    val amount: Double,
    val note: String = "",
    val date: Long,
)
