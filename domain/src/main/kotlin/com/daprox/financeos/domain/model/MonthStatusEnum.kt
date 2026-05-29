package com.daprox.financeos.domain.model

/**
 * Enum for month status/lifecycle state.
 *
 * Represents the user's assessment or classification of a month's financial health.
 * Used to tag months with labels for filtering, analysis, and historical tracking.
 */
enum class MonthStatusEnum {
    /** Best — excellent month, all budgets met, strong savings */
    BEST,

    /** Good — solid month, mostly on track */
    GOOD,

    /** Mid — average month, mixed results */
    MID,

    /** Hard — difficult month, budget constraints or unexpected expenses */
    HARD,
}
