package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.TransactionEntity
import com.daprox.financeos.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    envelopeId = envelopeId,
    monthId = monthId,
    amount = amount,
    note = note,
    date = date,
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    envelopeId = envelopeId,
    monthId = monthId,
    amount = amount,
    note = note,
    date = date,
)
