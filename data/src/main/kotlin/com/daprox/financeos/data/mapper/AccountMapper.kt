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

/**
 * Converts an [AccountEntity] (database model) to an [Account] (domain model).
 *
 * Maps the string account type stored in the database to the corresponding [AccountTypeEnum].
 * Falls back to [AccountTypeEnum.COURANT] if the type string is unrecognized.
 *
 * @return The domain model with enum type
 */
fun AccountEntity.toDomain(): Account = Account(
  id = id,
  name = name,
  type = ACCOUNT_TYPE_MAP[type] ?: AccountTypeEnum.COURANT,
  balance = balance,
  cap = cap,
  colorHex = colorHex,
)

/**
 * Converts an [Account] (domain model) to an [AccountEntity] (database model).
 *
 * Maps the [AccountTypeEnum] to its string representation for database storage.
 * Falls back to "Compte courant" if the enum value is unrecognized.
 *
 * @return The entity model with string type
 */
fun Account.toEntity(): AccountEntity = AccountEntity(
  id = id,
  name = name,
  type = ACCOUNT_TYPE_REVERSE[type] ?: "Compte courant",
  balance = balance,
  cap = cap,
  colorHex = colorHex,
)
