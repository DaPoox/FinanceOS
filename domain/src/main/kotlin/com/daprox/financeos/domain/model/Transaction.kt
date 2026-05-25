package com.daprox.financeos.domain.model

data class Transaction(
    val id: String,
    val envelopeId: String,
    val monthId: String,
    val amount: Double,
    val note: String = "",
    val date: Long,
)
