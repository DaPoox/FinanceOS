package com.daprox.financeos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "envelopes")
data class EnvelopeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,       // FIXED | VARIABLE | MONTHLY | PERMANENT | SAVINGS | INVESTMENT
    val iconKey: String,    // mapped to ImageVector via IconMapper in :presentation
    val colorHex: String,
    val isActive: Boolean = true,
)
