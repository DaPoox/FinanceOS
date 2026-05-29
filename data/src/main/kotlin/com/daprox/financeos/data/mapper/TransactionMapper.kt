package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.TransactionEntity
import com.daprox.financeos.domain.model.Transaction

/**
 * Converts a [TransactionEntity] (database model) to a [Transaction] (domain model).
 *
 * This is a direct mapping with no transformation needed.
 *
 * @return The domain model
 */
fun TransactionEntity.toDomain(): Transaction = Transaction(
  id = id,
  envelopeId = envelopeId,
  monthId = monthId,
  amount = amount,
  note = note,
  date = date,
)

/**
 * Converts a [Transaction] (domain model) to a [TransactionEntity] (database model).
 *
 * This is a direct mapping with no transformation needed.
 *
 * @return The entity model
 */
fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
  id = id,
  envelopeId = envelopeId,
  monthId = monthId,
  amount = amount,
  note = note,
  date = date,
)
