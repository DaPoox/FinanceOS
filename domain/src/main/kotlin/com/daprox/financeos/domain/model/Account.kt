package com.daprox.financeos.domain.model

data class Account(
    val id: String,
    val name: String,
    val type: AccountTypeEnum,
    val balance: Double,
    val cap: Double? = null,
    val colorHex: String,
)
