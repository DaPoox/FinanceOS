package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.MonthAllocationEntity
import com.daprox.financeos.domain.model.MonthAllocation

fun MonthAllocationEntity.toDomain(): MonthAllocation = MonthAllocation(
    monthId = monthId,
    envelopeId = envelopeId,
    allocated = allocated,
    accumulated = accumulated,
)

fun MonthAllocation.toEntity(): MonthAllocationEntity = MonthAllocationEntity(
    monthId = monthId,
    envelopeId = envelopeId,
    allocated = allocated,
    accumulated = accumulated,
)
