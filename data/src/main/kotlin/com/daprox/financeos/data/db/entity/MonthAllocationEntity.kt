package com.daprox.financeos.data.db.entity

import androidx.room.Entity

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
