package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.EnvelopeEntity
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.model.EnvelopeTypeEnum

/**
 * Converts an [EnvelopeEntity] (database model) to an [Envelope] (domain model).
 *
 * Maps the string envelope type stored in the database to the corresponding [EnvelopeTypeEnum].
 * The type string should match the enum name exactly (e.g., "FIXED", "VARIABLE", "PERMANENT").
 *
 * @return The domain model with enum type
 * @throws IllegalArgumentException if the type string does not match any [EnvelopeTypeEnum] value
 */
fun EnvelopeEntity.toDomain(): Envelope = Envelope(
  id = id,
  name = name,
  type = EnvelopeTypeEnum.valueOf(type),
  iconKey = iconKey,
  colorHex = colorHex,
  isActive = isActive,
)

/**
 * Converts an [Envelope] (domain model) to an [EnvelopeEntity] (database model).
 *
 * Maps the [EnvelopeTypeEnum] to its string representation (enum name) for database storage.
 *
 * @return The entity model with string type
 */
fun Envelope.toEntity(): EnvelopeEntity = EnvelopeEntity(
  id = id,
  name = name,
  type = type.name,
  iconKey = iconKey,
  colorHex = colorHex,
  isActive = isActive,
)
