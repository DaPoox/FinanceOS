package com.daprox.financeos.domain.model

data class MonthAllocation(
    val monthId: String,
    val envelopeId: String,
    val allocated: Double,
    val accumulated: Double = 0.0,
)
