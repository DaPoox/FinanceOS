package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.AccountEntity
import com.daprox.financeos.domain.model.Account
import com.daprox.financeos.domain.model.AccountTypeEnum

private val ACCOUNT_TYPE_MAP = mapOf(
    "Compte courant" to AccountTypeEnum.COURANT,
    "Épargne" to AccountTypeEnum.EPARGNE,
    "Investissement" to AccountTypeEnum.INVESTISSEMENT,
)

private val ACCOUNT_TYPE_REVERSE = ACCOUNT_TYPE_MAP.entries.associate { (k, v) -> v to k }

fun AccountEntity.toDomain(): Account = Account(
    id = id,
    name = name,
    type = ACCOUNT_TYPE_MAP[type] ?: AccountTypeEnum.COURANT,
    balance = balance,
    cap = cap,
    colorHex = colorHex,
)

fun Account.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    type = ACCOUNT_TYPE_REVERSE[type] ?: "Compte courant",
    balance = balance,
    cap = cap,
    colorHex = colorHex,
)
