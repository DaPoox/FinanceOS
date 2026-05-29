package com.daprox.financeos.presentation.history

/**
 * Status badge for a month's financial performance.
 *
 * Indicates savings rate or financial health across the month. Used to color-code
 * and visually communicate month rows in the history list.
 */
enum class MonthStatusEnum {
  /** Excellent savings performance. */
  BEST,

  /** Good savings performance. */
  GOOD,

  /** Moderate savings performance. */
  MID,

  /** Difficult month — low or negative savings. */
  HARD,
}
