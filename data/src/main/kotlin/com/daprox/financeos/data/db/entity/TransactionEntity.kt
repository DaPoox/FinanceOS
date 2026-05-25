package com.daprox.financeos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val envelopeId: String,
    val monthId: String,
    val amount: Double,
    val note: String = "",
    val date: Long,         // epoch millis
)
