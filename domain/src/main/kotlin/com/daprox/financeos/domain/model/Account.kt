package com.daprox.financeos.domain.model

/**
 * Represents a financial account (bank account, cash, investment, etc.).
 *
 * An account tracks the user's funds across different sources and types. Each account
 * maintains a current balance and optionally a maximum capacity limit.
 *
 * @property id Unique identifier for the account
 * @property name Display name of the account (e.g., "Checking", "Savings")
 * @property type Type of account (checking, savings, crypto, etc.) from [AccountTypeEnum]
 * @property balance Current balance amount in the account
 * @property cap Optional maximum balance limit; null means no limit
 * @property colorHex Hex color code for UI display
 */
data class Account(
    val id: String,
    val name: String,
    val type: AccountTypeEnum,
    val balance: Double,
    val cap: Double? = null,
    val colorHex: String,
)
