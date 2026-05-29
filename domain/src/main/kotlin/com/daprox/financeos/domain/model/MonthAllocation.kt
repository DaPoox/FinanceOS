package com.daprox.financeos.domain.model

/**
 * Represents the allocation of funds to an envelope for a specific month.
 *
 * Each allocation is the junction between a month's budget and a specific envelope.
 * The [allocated] field is the amount budgeted/assigned; [accumulated] tracks funds
 * remaining after spending (for permanent/carry-over envelopes).
 *
 * @property monthId Month identifier in "YYYY-MM" format (e.g., "2025-01")
 * @property envelopeId Unique identifier of the envelope being allocated to
 * @property allocated Amount of funds allocated to this envelope for the month
 * @property accumulated Remaining balance from previous months (carry-over/rollover amount).
 *   Applicable to permanent envelopes; 0.0 for monthly-reset envelopes
 */
data class MonthAllocation(
    val monthId: String,
    val envelopeId: String,
    val allocated: Double,
    val accumulated: Double = 0.0,
)
