package com.daprox.financeos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a monthly budget period.
 *
 * Each month tracks income, allocated amounts, spending, and contributions to savings/investments.
 * The status reflects the financial health of the month (best, good, mid, hard).
 *
 * @property id Primary key in format "YYYY-MM" (e.g., "2026-05" for May 2026)
 * @property label Display label (e.g., "Mai 2026")
 * @property income Total monthly income available for allocation
 * @property status Financial status: "best", "good", "mid", or "hard"
 * @property isAllocated Flag indicating whether allocations have been set for this month
 * @property spent Total amount spent in this month (sum of all transactions)
 * @property contrib Total amount contributed to savings/investments in this month
 */
@Entity(tableName = "months")
data class MonthEntity(
    @PrimaryKey val id: String,         // format "2026-05"
    val label: String,                  // "Mai 2026"
    val income: Double,
    val status: String = "good",        // best | good | mid | hard
    val isAllocated: Boolean = false,
    val spent: Double = 0.0,
    val contrib: Double = 0.0,
)
