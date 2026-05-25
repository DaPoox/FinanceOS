package com.daprox.financeos.domain.model

data class Month(
    val id: String,
    val label: String,
    val income: Double,
    val status: MonthStatusEnum,
    val isAllocated: Boolean = false,
    val spent: Double = 0.0,
    val contrib: Double = 0.0,
)
