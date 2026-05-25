package com.daprox.financeos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,           // "Compte courant" | "Épargne" | "Investissement"
    val balance: Double,
    val cap: Double? = null,    // legal cap (FR): Livret A 22950, LDDS 12000
    val colorHex: String,
)
