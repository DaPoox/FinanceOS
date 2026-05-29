package com.daprox.financeos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a single transaction within an envelope.
 *
 * Transactions track spending or contributions for a specific envelope during a specific month.
 * Each transaction is immutable once created and can only be deleted as a whole.
 *
 * @property id Unique identifier for the transaction
 * @property envelopeId Foreign key reference to the envelope this transaction belongs to
 * @property monthId Foreign key reference to the month this transaction belongs to (format "YYYY-MM")
 * @property amount Transaction amount in euros (positive for spending, could be negative for adjustments)
 * @property note Optional note describing the transaction (e.g., "Le Servan", "Carrefour Express")
 * @property date Transaction date as epoch milliseconds (UTC)
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val envelopeId: String,
    val monthId: String,
    val amount: Double,
    val note: String = "",
    val date: Long,         // epoch millis
)
