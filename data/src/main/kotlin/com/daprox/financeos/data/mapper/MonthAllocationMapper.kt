package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.MonthAllocationEntity
import com.daprox.financeos.domain.model.MonthAllocation

/**
 * Converts a [MonthAllocationEntity] (database model) to a [MonthAllocation] (domain model).
 *
 * This is a direct mapping with no transformation needed.
 *
 * @return The domain model
 */
fun MonthAllocationEntity.toDomain(): MonthAllocation = MonthAllocation(
  monthId = monthId,
  envelopeId = envelopeId,
  allocated = allocated,
  accumulated = accumulated,
)

/**
 * Converts a [MonthAllocation] (domain model) to a [MonthAllocationEntity] (database model).
 *
 * This is a direct mapping with no transformation needed.
 *
 * @return The entity model
 */
fun MonthAllocation.toEntity(): MonthAllocationEntity = MonthAllocationEntity(
  monthId = monthId,
  envelopeId = envelopeId,
  allocated = allocated,
  accumulated = accumulated,
)
