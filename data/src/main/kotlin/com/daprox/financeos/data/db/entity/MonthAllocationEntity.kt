package com.daprox.financeos.data.db.entity

import androidx.room.Entity

/**
 * Entity representing the allocation of an envelope for a specific month.
 *
 * Each allocation defines how much budget is assigned to an envelope for a given month.
 * For PERMANENT type envelopes, the [accumulated] field tracks contributions that carry over
 * month to month until they are fully spent.
 *
 * Uses a composite primary key of (monthId, envelopeId) to ensure one allocation per envelope per month.
 *
 * @property monthId Foreign key to the month (format "YYYY-MM")
 * @property envelopeId Foreign key to the envelope
 * @property allocated Amount allocated to this envelope for this month in euros
 * @property accumulated Accumulated balance for PERMANENT type envelopes only
 *                       Carries over month to month; reset to 0.0 for other envelope types
 */
@Entity(
    tableName = "month_allocations",
    primaryKeys = ["monthId", "envelopeId"],
)
data class MonthAllocationEntity(
    val monthId: String,
    val envelopeId: String,
    val allocated: Double,
    val accumulated: Double = 0.0,      // only used for PERMANENT type
)
