package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.MonthEntity
import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.model.MonthStatusEnum

fun MonthEntity.toDomain(): Month = Month(
    id = id,
    label = label,
    income = income,
    status = MonthStatusEnum.valueOf(status.uppercase()),
    isAllocated = isAllocated,
    spent = spent,
    contrib = contrib,
)

fun Month.toEntity(): MonthEntity = MonthEntity(
    id = id,
    label = label,
    income = income,
    status = status.name.lowercase(),
    isAllocated = isAllocated,
    spent = spent,
    contrib = contrib,
)
