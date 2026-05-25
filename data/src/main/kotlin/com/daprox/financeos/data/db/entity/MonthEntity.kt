package com.daprox.financeos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
