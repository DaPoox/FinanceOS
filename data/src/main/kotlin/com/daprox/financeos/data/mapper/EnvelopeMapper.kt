package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.EnvelopeEntity
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.model.EnvelopeTypeEnum

fun EnvelopeEntity.toDomain(): Envelope = Envelope(
    id = id,
    name = name,
    type = EnvelopeTypeEnum.valueOf(type),
    iconKey = iconKey,
    colorHex = colorHex,
    isActive = isActive,
)

fun Envelope.toEntity(): EnvelopeEntity = EnvelopeEntity(
    id = id,
    name = name,
    type = type.name,
    iconKey = iconKey,
    colorHex = colorHex,
    isActive = isActive,
)
