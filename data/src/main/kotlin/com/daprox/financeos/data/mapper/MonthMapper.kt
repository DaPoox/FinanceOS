package com.daprox.financeos.data.mapper

import com.daprox.financeos.data.db.entity.MonthEntity
import com.daprox.financeos.domain.model.Month
import com.daprox.financeos.domain.model.MonthStatusEnum

/**
 * Converts a [MonthEntity] (database model) to a [Month] (domain model).
 *
 * Maps the lowercase status string stored in the database to the corresponding [MonthStatusEnum].
 * The status is uppercased for enum matching (e.g., "good" -> "GOOD").
 *
 * @return The domain model with enum status
 * @throws IllegalArgumentException if the status string does not match any [MonthStatusEnum] value
 */
fun MonthEntity.toDomain(): Month = Month(
  id = id,
  label = label,
  income = income,
  status = MonthStatusEnum.valueOf(status.uppercase()),
  isAllocated = isAllocated,
  spent = spent,
  contrib = contrib,
)

/**
 * Converts a [Month] (domain model) to a [MonthEntity] (database model).
 *
 * Maps the [MonthStatusEnum] to its lowercase string representation for database storage.
 * (e.g., "GOOD" -> "good").
 *
 * @return The entity model with lowercase string status
 */
fun Month.toEntity(): MonthEntity = MonthEntity(
  id = id,
  label = label,
  income = income,
  status = status.name.lowercase(),
  isAllocated = isAllocated,
  spent = spent,
  contrib = contrib,
)
