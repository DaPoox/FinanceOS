package com.daprox.financeos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a budget envelope (budget category).
 *
 * Envelopes are the core budget mechanism in FinanceOS. Each envelope represents a spending
 * or saving category that can hold allocations and transactions. Envelopes support soft deletion
 * via the [isActive] flag rather than hard deletion.
 *
 * @property id Unique identifier for the envelope
 * @property name Display name (e.g., "Loyer", "Courses", "Vacances")
 * @property type Envelope category: FIXED, VARIABLE, MONTHLY, PERMANENT, SAVINGS, or INVESTMENT
 * @property iconKey Icon identifier (e.g., "house", "shopping_cart", "piggy_bank")
 *                    Mapped to ImageVector via IconMapper in the :presentation module
 * @property colorHex Hex color code for UI display (e.g., "#8ba4be")
 * @property isActive Flag for soft deletion; inactive envelopes are hidden from the active list
 */
@Entity(tableName = "envelopes")
data class EnvelopeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,       // FIXED | VARIABLE | MONTHLY | PERMANENT | SAVINGS | INVESTMENT
    val iconKey: String,    // mapped to ImageVector via IconMapper in :presentation
    val colorHex: String,
    val isActive: Boolean = true,
)
