package com.daprox.financeos.domain.model

/**
 * Represents a monthly budget period.
 *
 * Each month tracks income, allocation status, and spending metrics. The [isAllocated] flag
 * indicates whether funds have been distributed to envelopes for this month. Months follow
 * the envelope budgeting method: income is allocated to envelopes, and transactions draw from
 * those envelopes.
 *
 * @property id Month identifier in "YYYY-MM" format (e.g., "2025-01")
 * @property label Human-readable month label (e.g., "January 2025")
 * @property income Total income received in the month
 * @property status Current lifecycle status (active, planning, archived) from [MonthStatusEnum]
 * @property isAllocated Whether the monthly budget has been allocated to envelopes.
 *   True indicates the allocation step is complete; false means allocation is pending
 * @property spent Total amount spent across all envelopes in the month
 * @property contrib Total contributed/allocated to all envelopes in the month
 */
data class Month(
    val id: String,
    val label: String,
    val income: Double,
    val status: MonthStatusEnum,
    val isAllocated: Boolean = false,
    val spent: Double = 0.0,
    val contrib: Double = 0.0,
)
