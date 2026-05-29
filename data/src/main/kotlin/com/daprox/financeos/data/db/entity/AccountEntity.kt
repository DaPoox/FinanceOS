package com.daprox.financeos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a bank or savings account.
 *
 * Accounts hold money and can be of different types (checking, savings, investment).
 * Each account can have a legal cap (e.g., Livret A in France is capped at €22,950).
 *
 * @property id Unique identifier for the account
 * @property name Display name (e.g., "Boursorama", "Livret A")
 * @property type Account type: "Compte courant", "Épargne", or "Investissement"
 * @property balance Current balance in euros
 * @property cap Optional legal maximum balance (e.g., 22950.0 for Livret A, 12000.0 for LDDS)
 * @property colorHex Hex color code for UI display (e.g., "#e8eef5")
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,           // "Compte courant" | "Épargne" | "Investissement"
    val balance: Double,
    val cap: Double? = null,    // legal cap (FR): Livret A 22950, LDDS 12000
    val colorHex: String,
)
